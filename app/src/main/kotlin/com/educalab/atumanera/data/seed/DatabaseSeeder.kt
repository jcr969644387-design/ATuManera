package com.educalab.atumanera.data.seed

import com.educalab.atumanera.data.local.AppDatabase
import com.educalab.atumanera.data.local.entity.CityEntity
import com.educalab.atumanera.data.local.entity.CityTileEntity
import com.educalab.atumanera.data.local.entity.MissionEntity
import com.educalab.atumanera.data.local.entity.MissionRequirementEntity
import com.educalab.atumanera.data.local.entity.UserProfileEntity

/** Tamaño por defecto de la cuadrícula de una ciudad nueva. */
const val DEFAULT_CITY_ROWS = 10
const val DEFAULT_CITY_COLS = 10
const val DEFAULT_CITY_BUDGET = 2500

/**
 * Puebla la base de datos en el primer arranque: catálogo de infraestructuras,
 * insignias, decoraciones y las misiones (4 niveles de dificultad). También
 * crea el perfil, la ciudad y la cuadrícula inicial si todavía no existen.
 */
class DatabaseSeeder(private val db: AppDatabase) {

    suspend fun seedCatalogIfNeeded() {
        if (db.infrastructureTypeDao().count() == 0) {
            db.infrastructureTypeDao().insertAll(CatalogSeed.infrastructureTypes())
        }
        if (db.badgeDao().count() == 0) {
            db.badgeDao().insertAll(CatalogSeed.badges())
        }
        if (db.decorationDao().count() == 0) {
            db.decorationDao().insertAll(CatalogSeed.decorations())
        }
        // Se inserta cada misión por su código único, no solo en el primer
        // arranque: así, cuando se agregan misiones nuevas en una
        // actualización, también aparecen en instalaciones ya existentes.
        for (seed in MissionSeed.missions()) {
            if (db.missionDao().getByCode(seed.code) != null) continue
            val missionId = db.missionDao().insert(
                MissionEntity(0, seed.code, seed.title, seed.description, seed.category, seed.order, seed.rewardXp, seed.rewardBadgeCode)
            )
            if (seed.requirements.isNotEmpty()) {
                db.missionRequirementDao().insertAll(
                    seed.requirements.map { req ->
                        MissionRequirementEntity(0, missionId, req.type.name, req.key, req.target)
                    }
                )
            }
        }
    }

    suspend fun ensureUserAndCity(): Pair<UserProfileEntity, CityEntity> {
        val existingUser = db.userProfileDao().getFirst()
        val user = existingUser ?: run {
            val id = db.userProfileDao().insert(
                UserProfileEntity(0, alias = "Alcalde", avatarCode = "avatar_1", createdAt = System.currentTimeMillis())
            )
            UserProfileEntity(id, "Alcalde", "avatar_1", System.currentTimeMillis())
        }

        val existingCity = db.cityDao().getLatestForUser(user.id)
        val city = existingCity ?: run {
            val now = System.currentTimeMillis()
            val cityId = db.cityDao().insert(
                CityEntity(
                    0, user.id, "Mi Ciudad", DEFAULT_CITY_BUDGET,
                    DEFAULT_CITY_ROWS, DEFAULT_CITY_COLS, now, now
                )
            )
            val tiles = mutableListOf<CityTileEntity>()
            for (r in 0 until DEFAULT_CITY_ROWS) {
                for (c in 0 until DEFAULT_CITY_COLS) {
                    tiles.add(CityTileEntity(0, cityId, r, c, true))
                }
            }
            db.cityTileDao().insertAll(tiles)
            db.cityDao().getById(cityId)!!
        }

        return user to city
    }
}
