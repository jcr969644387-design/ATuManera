package com.educalab.atumanera.data.repository

import com.educalab.atumanera.data.local.AppDatabase
import com.educalab.atumanera.data.local.entity.CityEntity
import com.educalab.atumanera.data.local.entity.CityMetricEntity
import com.educalab.atumanera.data.local.entity.CityTileEntity
import com.educalab.atumanera.data.local.entity.MissionProgressEntity
import com.educalab.atumanera.data.local.entity.PlacedInfrastructureEntity
import com.educalab.atumanera.data.local.entity.ProgressEntity
import com.educalab.atumanera.data.local.entity.RoadConnectionEntity
import com.educalab.atumanera.data.local.entity.ServiceCoverageEntity
import com.educalab.atumanera.data.local.entity.UnlockedDecorationEntity
import com.educalab.atumanera.data.local.entity.UserBadgeEntity
import com.educalab.atumanera.domain.logic.BudgetManager
import com.educalab.atumanera.domain.logic.BudgetResult
import com.educalab.atumanera.domain.logic.CityStateSnapshot
import com.educalab.atumanera.domain.logic.GridEngine
import com.educalab.atumanera.domain.logic.MetricsCalculator
import com.educalab.atumanera.domain.logic.MissionEvaluator
import com.educalab.atumanera.domain.logic.MissionRequirementInput
import com.educalab.atumanera.domain.logic.UnlockContext
import com.educalab.atumanera.domain.logic.UnlockEvaluator
import com.educalab.atumanera.domain.model.CityMetricsSnapshot
import com.educalab.atumanera.domain.model.GridPosition
import com.educalab.atumanera.domain.model.InfraCategory
import com.educalab.atumanera.domain.model.RequirementType
import com.educalab.atumanera.domain.model.TileSnapshot

/** Resultado de intentar colocar una infraestructura sobre la cuadrícula. */
sealed class PlacementOutcome {
    data class Success(
        val metrics: CityMetricsSnapshot,
        val newlyCompletedMissions: List<String>,
        val newBadges: List<String>,
        val newlyCompletedLevels: List<Int> = emptyList()
    ) : PlacementOutcome()
    object TileOccupied : PlacementOutcome()
    object TileNotFound : PlacementOutcome()
    object InsufficientBudget : PlacementOutcome()
}

sealed class RemovalOutcome {
    data class Success(val metrics: CityMetricsSnapshot) : RemovalOutcome()
    object NothingToRemove : RemovalOutcome()
}

/**
 * Repositorio central de la simulación urbana. Coordina Room con el motor de
 * reglas de dominio: nunca ejecuta SQL desde la UI ni contiene reglas en los
 * Composables.
 */
class CityRepository(private val db: AppDatabase) {

    private val budgetManager = BudgetManager()
    private val missionEvaluator = MissionEvaluator()
    private val unlockEvaluator = UnlockEvaluator()

    companion object {
        // Rangos de orderIndex que definen cada nivel de dificultad de misiones.
        private val LEVEL_1_RANGE = 1..30
        private val LEVEL_2_RANGE = 31..48
        private val LEVEL_3_RANGE = 49..59
        private val LEVEL_4_RANGE = 60..67

        // Presupuesto adicional que se desbloquea al completar cada nivel por completo.
        private const val LEVEL_1_BUDGET_BONUS = 2000
        private const val LEVEL_2_BUDGET_BONUS = 3000
        private const val LEVEL_3_BUDGET_BONUS = 4500
        private const val LEVEL_4_BUDGET_BONUS = 6000

        /** Nombre reservado que identifica la ciudad de Modo Libre (evita tocar el esquema de Room). */
        const val FREE_MODE_CITY_NAME = "Modo Libre"
        private const val FREE_MODE_BUDGET = 999_999_999
    }

    // ---------- Lectura de estado ----------

    suspend fun buildGridSnapshot(cityId: Long): List<TileSnapshot> {
        val tiles = db.cityTileDao().getTilesForCity(cityId)
        val placements = db.placedInfrastructureDao().getEnrichedForCity(cityId)
        val byTileId = placements.associateBy { it.tileId }

        return tiles.map { tile ->
            val placement = byTileId[tile.id]
            TileSnapshot(
                position = GridPosition(tile.row, tile.col),
                infraCode = placement?.infraCode,
                category = placement?.infraCategory?.let { InfraCategory.valueOf(it) },
                coverageRadius = placement?.infraCoverageRadius ?: 0
            )
        }
    }

    suspend fun currentMetrics(cityId: Long): CityMetricsSnapshot {
        val city = db.cityDao().getById(cityId) ?: error("Ciudad no encontrada")
        val engine = GridEngine(city.rows, city.cols)
        val calculator = MetricsCalculator(engine)
        val tiles = buildGridSnapshot(cityId)
        val spent = db.placedInfrastructureDao().totalSpent(cityId)
        return calculator.calculate(tiles, spent, city.budgetTotal)
    }

    fun observeUserFlow() = db.userProfileDao().observeFirst()
    fun observeCityFlow(userId: Long) = db.cityDao().observeLatestForUser(userId)
    fun observeFreeCityFlow(userId: Long) = db.cityDao().observeFreeCityForUser(userId)
    fun observeMetricsHistory(cityId: Long) = db.cityMetricDao().observeHistory(cityId)
    fun observeMissionProgress(userId: Long, cityId: Long) = db.missionProgressDao().observeForCity(userId, cityId)
    fun observeMissions() = db.missionDao().observeAll()
    fun observeBadges() = db.badgeDao().observeAll()
    fun observeUserBadges(userId: Long) = db.userBadgeDao().observeForUser(userId)
    fun observeDecorations() = db.decorationDao().observeAll()
    fun observeUnlockedDecorations(userId: Long) = db.unlockedDecorationDao().observeForUser(userId)
    fun observeProgress(userId: Long) = db.progressDao().observeForUser(userId)
    fun observeInfrastructureCatalog() = db.infrastructureTypeDao().observeAll()
    fun observeCatalogByCategory(category: InfraCategory) = db.infrastructureTypeDao().observeByCategory(category.name)
    fun observePlacements(cityId: Long) = db.placedInfrastructureDao().observeForCity(cityId)
    fun observeTiles(cityId: Long) = db.cityTileDao().observeTilesForCity(cityId)

    suspend fun updateProfile(userId: Long, alias: String, avatarCode: String) {
        val current = db.userProfileDao().getById(userId) ?: return
        db.userProfileDao().update(current.copy(alias = alias, avatarCode = avatarCode))
    }

    // ---------- Escritura: colocar / quitar infraestructura ----------

    suspend fun placeInfrastructure(userId: Long, cityId: Long, row: Int, col: Int, infrastructureTypeId: Long): PlacementOutcome {
        val city = db.cityDao().getById(cityId) ?: return PlacementOutcome.TileNotFound
        val tile = db.cityTileDao().getTileAt(cityId, row, col) ?: return PlacementOutcome.TileNotFound

        val existing = db.placedInfrastructureDao().getAt(cityId, tile.id)
        if (existing != null) return PlacementOutcome.TileOccupied

        val infraType = db.infrastructureTypeDao().getById(infrastructureTypeId) ?: return PlacementOutcome.TileNotFound
        val spent = db.placedInfrastructureDao().totalSpent(cityId)

        when (val budgetResult = budgetManager.place(spent, city.budgetTotal, infraType.cost)) {
            is BudgetResult.Rejected -> return PlacementOutcome.InsufficientBudget
            is BudgetResult.Approved -> Unit
        }

        db.placedInfrastructureDao().insert(
            PlacedInfrastructureEntity(0, cityId, tile.id, infrastructureTypeId, System.currentTimeMillis())
        )

        if (infraType.category == InfraCategory.ROAD.name) {
            linkRoadNeighbors(city.rows, city.cols, cityId, tile.id, row, col)
        }

        return finalizeCityUpdate(userId, cityId)
    }

    suspend fun removeInfrastructure(userId: Long, cityId: Long, row: Int, col: Int): RemovalOutcome {
        val tile = db.cityTileDao().getTileAt(cityId, row, col) ?: return RemovalOutcome.NothingToRemove
        val existing = db.placedInfrastructureDao().getAt(cityId, tile.id) ?: return RemovalOutcome.NothingToRemove

        db.placedInfrastructureDao().removeAt(cityId, tile.id)
        db.roadConnectionDao().removeInvolvingTile(cityId, tile.id)

        val outcome = finalizeCityUpdate(userId, cityId)
        val metrics = (outcome as? PlacementOutcome.Success)?.metrics ?: currentMetrics(cityId)
        return RemovalOutcome.Success(metrics)
    }

    /** Elimina todas las construcciones de la ciudad de una sola vez y recalcula todo el estado. */
    suspend fun clearCity(userId: Long, cityId: Long): PlacementOutcome.Success {
        db.placedInfrastructureDao().clearCity(cityId)
        db.roadConnectionDao().clearCity(cityId)
        return finalizeCityUpdate(userId, cityId)
    }

    /** Elimina únicamente las construcciones de una categoría (p. ej. solo las calles). */
    suspend fun clearCategory(userId: Long, cityId: Long, category: InfraCategory): PlacementOutcome.Success {
        db.placedInfrastructureDao().clearCategory(cityId, category.name)
        if (category == InfraCategory.ROAD) {
            db.roadConnectionDao().clearCity(cityId)
        }
        return finalizeCityUpdate(userId, cityId)
    }

    // ---------- Modo Libre: ciudad aparte, sin presupuesto ni misiones ----------
    // Se identifica por su nombre ("Modo Libre") para no requerir cambios de
    // esquema en Room. Construir ahí nunca evalúa misiones, insignias ni
    // historial de métricas: es un espacio de juego libre y separado.

    /** Crea (si no existe) la ciudad de Modo Libre del usuario y la devuelve. */
    suspend fun ensureFreeCity(userId: Long, rows: Int, cols: Int): CityEntity {
        db.cityDao().getFreeCityForUser(userId)?.let { return it }
        val now = System.currentTimeMillis()
        val cityId = db.cityDao().insert(
            CityEntity(0, userId, FREE_MODE_CITY_NAME, FREE_MODE_BUDGET, rows, cols, now, now)
        )
        val tiles = mutableListOf<CityTileEntity>()
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                tiles.add(CityTileEntity(0, cityId, r, c, true))
            }
        }
        db.cityTileDao().insertAll(tiles)
        return db.cityDao().getById(cityId)!!
    }

    suspend fun placeInfrastructureFree(cityId: Long, row: Int, col: Int, infrastructureTypeId: Long): PlacementOutcome {
        val city = db.cityDao().getById(cityId) ?: return PlacementOutcome.TileNotFound
        val tile = db.cityTileDao().getTileAt(cityId, row, col) ?: return PlacementOutcome.TileNotFound
        val existing = db.placedInfrastructureDao().getAt(cityId, tile.id)
        if (existing != null) return PlacementOutcome.TileOccupied
        val infraType = db.infrastructureTypeDao().getById(infrastructureTypeId) ?: return PlacementOutcome.TileNotFound

        db.placedInfrastructureDao().insert(
            PlacedInfrastructureEntity(0, cityId, tile.id, infrastructureTypeId, System.currentTimeMillis())
        )
        if (infraType.category == InfraCategory.ROAD.name) {
            linkRoadNeighbors(city.rows, city.cols, cityId, tile.id, row, col)
        }
        return PlacementOutcome.Success(currentMetrics(cityId), emptyList(), emptyList())
    }

    suspend fun removeInfrastructureFree(cityId: Long, row: Int, col: Int): RemovalOutcome {
        val tile = db.cityTileDao().getTileAt(cityId, row, col) ?: return RemovalOutcome.NothingToRemove
        val existing = db.placedInfrastructureDao().getAt(cityId, tile.id) ?: return RemovalOutcome.NothingToRemove
        db.placedInfrastructureDao().removeAt(cityId, tile.id)
        db.roadConnectionDao().removeInvolvingTile(cityId, tile.id)
        return RemovalOutcome.Success(currentMetrics(cityId))
    }

    suspend fun clearCityFree(cityId: Long): PlacementOutcome.Success {
        db.placedInfrastructureDao().clearCity(cityId)
        db.roadConnectionDao().clearCity(cityId)
        return PlacementOutcome.Success(currentMetrics(cityId), emptyList(), emptyList())
    }

    suspend fun clearCategoryFree(cityId: Long, category: InfraCategory): PlacementOutcome.Success {
        db.placedInfrastructureDao().clearCategory(cityId, category.name)
        if (category == InfraCategory.ROAD) {
            db.roadConnectionDao().clearCity(cityId)
        }
        return PlacementOutcome.Success(currentMetrics(cityId), emptyList(), emptyList())
    }

    private suspend fun linkRoadNeighbors(rows: Int, cols: Int, cityId: Long, tileId: Long, row: Int, col: Int) {
        val neighborOffsets = listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)
        val connections = mutableListOf<RoadConnectionEntity>()
        for ((dr, dc) in neighborOffsets) {
            val nr = row + dr
            val nc = col + dc
            if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue
            val neighborTile = db.cityTileDao().getTileAt(cityId, nr, nc) ?: continue
            val neighborPlacement = db.placedInfrastructureDao().getAt(cityId, neighborTile.id) ?: continue
            val neighborType = db.infrastructureTypeDao().getById(neighborPlacement.infrastructureTypeId) ?: continue
            if (neighborType.category == InfraCategory.ROAD.name) {
                connections.add(RoadConnectionEntity(0, cityId, tileId, neighborTile.id))
            }
        }
        if (connections.isNotEmpty()) db.roadConnectionDao().insertAll(connections)
    }

    // ---------- Recalculo integral tras cada cambio ----------

    private suspend fun finalizeCityUpdate(userId: Long, cityId: Long): PlacementOutcome.Success {
        val city = db.cityDao().getById(cityId)!!
        val engine = GridEngine(city.rows, city.cols)
        val calculator = MetricsCalculator(engine)
        val tiles = buildGridSnapshot(cityId)
        val spent = db.placedInfrastructureDao().totalSpent(cityId)
        val metrics = calculator.calculate(tiles, spent, city.budgetTotal)

        // Persistir cobertura real por categoría de servicio.
        val coverageCounts = mutableMapOf<String, Int>()
        for (category in listOf(InfraCategory.EDUCATION, InfraCategory.HEALTH, InfraCategory.WATER, InfraCategory.PARK)) {
            val coverage = engine.computeCoverage(tiles, category)
            db.serviceCoverageDao().clearCategory(cityId, category.name)
            val rows = tiles.filter { it.category == InfraCategory.HOUSING }.mapNotNull { house ->
                val tile = db.cityTileDao().getTileAt(cityId, house.position.row, house.position.col) ?: return@mapNotNull null
                ServiceCoverageEntity(
                    0, cityId, tile.id, category.name,
                    covered = house.position in coverage.coveredHouses,
                    computedAt = System.currentTimeMillis()
                )
            }
            if (rows.isNotEmpty()) db.serviceCoverageDao().insertAll(rows)
            coverageCounts[category.name] = coverage.coveredHouses.size
        }

        // Guardar una instantánea histórica de métricas (recortando historial largo).
        db.cityMetricDao().insert(
            CityMetricEntity(
                0, cityId, System.currentTimeMillis(),
                metrics.mobility, metrics.servicesScore, metrics.greenScore,
                metrics.educationCoverage, metrics.healthCoverage, metrics.waterCoverage,
                metrics.budgetSpent, metrics.budgetTotal
            )
        )
        db.cityMetricDao().trimHistory(cityId, keep = 100)

        // Evaluar misiones reales contra el estado actual.
        val placedCounts = db.placedInfrastructureDao().countsByCode(cityId).associate { it.code to it.count }
        val roadNetwork = engine.computeRoadNetwork(tiles)
        val state = CityStateSnapshot(
            placedCounts = placedCounts,
            metrics = metrics,
            largestRoadComponent = roadNetwork.largestComponentSize(),
            coverageCounts = coverageCounts
        )

        val progress = db.progressDao().getForUser(userId) ?: ProgressEntity(0, userId, cityId, 1, 0, 0, System.currentTimeMillis())
        var totalXp = progress.totalXp
        var missionsCompleted = progress.missionsCompleted
        val newlyCompleted = mutableListOf<String>()
        val completedMissionIds = mutableSetOf<Long>()

        // Las misiones se evalúan nivel por nivel, en orden. Un nivel solo se
        // evalúa (y por tanto solo puede completarse) si el nivel anterior ya
        // está 100% completo; mientras tanto sus misiones quedan BLOQUEADAS.
        val allMissions = db.missionDao().getAllList()
        val levelRanges = listOf(LEVEL_1_RANGE, LEVEL_2_RANGE, LEVEL_3_RANGE, LEVEL_4_RANGE)
        var previousLevelUnlocked = true

        for (range in levelRanges) {
            val missionsInLevel = allMissions.filter { it.orderIndex in range }

            for (mission in missionsInLevel) {
                val existingProgress = db.missionProgressDao().getFor(userId, cityId, mission.id)
                val alreadyCompleted = existingProgress?.status == "COMPLETED"

                if (alreadyCompleted) {
                    completedMissionIds.add(mission.id)
                    continue
                }

                if (!previousLevelUnlocked) {
                    if (existingProgress?.status != "LOCKED") {
                        db.missionProgressDao().upsert(
                            MissionProgressEntity(0, userId, cityId, mission.id, "LOCKED", 0, null)
                        )
                    }
                    continue
                }

                val requirements = db.missionRequirementDao().getForMission(mission.id).map {
                    MissionRequirementInput(RequirementType.valueOf(it.type), it.requirementKey, it.targetValue)
                }
                val evaluation = missionEvaluator.evaluate(mission.id, requirements, state)

                if (evaluation.isComplete) {
                    db.missionProgressDao().upsert(
                        MissionProgressEntity(0, userId, cityId, mission.id, "COMPLETED", 100, System.currentTimeMillis())
                    )
                    totalXp += mission.rewardXp
                    missionsCompleted += 1
                    newlyCompleted.add(mission.code)
                    completedMissionIds.add(mission.id)
                } else {
                    val status = if (evaluation.progressPercent > 0) "IN_PROGRESS" else "AVAILABLE"
                    db.missionProgressDao().upsert(
                        MissionProgressEntity(0, userId, cityId, mission.id, status, evaluation.progressPercent, null)
                    )
                }
            }

            previousLevelUnlocked = missionsInLevel.isNotEmpty() && missionsInLevel.all { it.id in completedMissionIds }
        }

        // Al completar cada nivel de misiones por completo se libera presupuesto
        // adicional para poder afrontar el siguiente nivel, más exigente.
        // progress.currentChapter guarda el próximo nivel pendiente de recompensar
        // (1 = aún no se recompensó el nivel 1) para no otorgar el bono dos veces.
        fun levelComplete(range: IntRange): Boolean {
            val idsInLevel = allMissions.filter { it.orderIndex in range }.map { it.id }
            return idsInLevel.isNotEmpty() && idsInLevel.all { it in completedMissionIds }
        }

        val level1Complete = levelComplete(LEVEL_1_RANGE)
        val level2Complete = levelComplete(LEVEL_2_RANGE)
        val level3Complete = levelComplete(LEVEL_3_RANGE)
        val level4Complete = levelComplete(LEVEL_4_RANGE)

        var currentChapter = progress.currentChapter
        var budgetTotal = city.budgetTotal
        val newlyCompletedLevels = mutableListOf<Int>()
        if (currentChapter <= 1 && level1Complete) {
            budgetTotal += LEVEL_1_BUDGET_BONUS
            currentChapter = 2
            newlyCompletedLevels.add(1)
        }
        if (currentChapter <= 2 && level2Complete) {
            budgetTotal += LEVEL_2_BUDGET_BONUS
            currentChapter = 3
            newlyCompletedLevels.add(2)
        }
        if (currentChapter <= 3 && level3Complete) {
            budgetTotal += LEVEL_3_BUDGET_BONUS
            currentChapter = 4
            newlyCompletedLevels.add(3)
        }
        if (currentChapter <= 4 && level4Complete) {
            budgetTotal += LEVEL_4_BUDGET_BONUS
            currentChapter = 5
            newlyCompletedLevels.add(4)
        }
        if (budgetTotal != city.budgetTotal) {
            db.cityDao().update(city.copy(budgetTotal = budgetTotal, updatedAt = System.currentTimeMillis()))
        }

        db.progressDao().upsert(
            progress.copy(userId = userId, cityId = cityId, currentChapter = currentChapter, totalXp = totalXp, missionsCompleted = missionsCompleted, updatedAt = System.currentTimeMillis())
        )

        // Evaluar insignias y decoraciones desbloqueables.
        val unlockContext = UnlockContext(totalXp, missionsCompleted, metrics, placedCounts, level1Complete, level2Complete, level3Complete, level4Complete)
        val newBadgeCodes = mutableListOf<String>()
        val earnedBadgeIds = db.userBadgeDao().getEarnedBadgeIds(userId).toSet()
        for (badgeCode in unlockEvaluator.evaluateBadges(unlockContext, UnlockEvaluator.defaultBadgeConditions())) {
            val badge = db.badgeDao().getByCode(badgeCode) ?: continue
            if (badge.id !in earnedBadgeIds) {
                db.userBadgeDao().insert(UserBadgeEntity(0, userId, badge.id, System.currentTimeMillis()))
                newBadgeCodes.add(badgeCode)
            }
        }

        val unlockedDecoIds = db.unlockedDecorationDao().getUnlockedIds(userId).toSet()
        for (decoCode in unlockEvaluator.evaluateDecorations(unlockContext, UnlockEvaluator.defaultDecorationConditions())) {
            val deco = db.decorationDao().getByCode(decoCode) ?: continue
            if (deco.id !in unlockedDecoIds) {
                db.unlockedDecorationDao().insert(UnlockedDecorationEntity(0, userId, deco.id, System.currentTimeMillis()))
            }
        }

        return PlacementOutcome.Success(metrics, newlyCompleted, newBadgeCodes, newlyCompletedLevels)
    }
}
