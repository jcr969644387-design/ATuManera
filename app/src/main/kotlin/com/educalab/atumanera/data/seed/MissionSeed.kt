package com.educalab.atumanera.data.seed

import com.educalab.atumanera.domain.model.RequirementType

/** Requisito semilla de una misión (se traduce a MissionRequirementEntity). */
data class RequirementSeed(val type: RequirementType, val key: String, val target: Int)

/** Definición semilla completa de una misión. */
data class MissionSeedDefinition(
    val code: String,
    val title: String,
    val description: String,
    val category: String,
    val order: Int,
    val rewardXp: Int,
    val rewardBadgeCode: String?,
    val requirements: List<RequirementSeed>
)

/**
 * 30 misiones que guían el progreso del jugador desde la primera calle
 * hasta una ciudad con buena cobertura de servicios. Todas se evalúan con
 * [com.educalab.atumanera.domain.logic.MissionEvaluator] contra datos reales.
 */
object MissionSeed {

    fun missions(): List<MissionSeedDefinition> = listOf(
        MissionSeedDefinition("M01", "Traza la primera calle", "Toda ciudad empieza con un camino. Coloca tu primera calle.", "ROAD", 1, 20, "BADGE_FIRST_ROAD",
            listOf(RequirementSeed(RequirementType.PLACE_COUNT, "ROAD_BASIC", 1))),
        MissionSeedDefinition("M02", "Un techo para empezar", "Construye tu primera vivienda junto a la calle.", "HOUSING", 2, 20, "BADGE_FIRST_HOUSE",
            listOf(RequirementSeed(RequirementType.PLACE_COUNT, "HOUSE_SMALL", 1))),
        MissionSeedDefinition("M03", "Conecta tres casas", "Une al menos 3 casas a la misma red de calles.", "ROAD", 3, 25, null,
            listOf(RequirementSeed(RequirementType.PLACE_COUNT, "HOUSE_SMALL", 3), RequirementSeed(RequirementType.ROAD_NETWORK_SIZE, "ANY", 3))),
        MissionSeedDefinition("M04", "Amplía el barrio", "Construye un bloque de viviendas para más familias.", "HOUSING", 4, 25, null,
            listOf(RequirementSeed(RequirementType.PLACE_COUNT, "HOUSE_BLOCK", 1))),
        MissionSeedDefinition("M05", "La primera escuela", "Da acceso a la educación construyendo una escuela.", "EDUCATION", 5, 30, "BADGE_FIRST_SCHOOL",
            listOf(RequirementSeed(RequirementType.PLACE_COUNT, "SCHOOL_PRIMARY", 1))),
        MissionSeedDefinition("M06", "Camino a clase", "Consigue que al menos 3 casas tengan acceso a la escuela.", "EDUCATION", 6, 30, null,
            listOf(RequirementSeed(RequirementType.COVERAGE_COUNT, "EDUCATION", 3))),
        MissionSeedDefinition("M07", "Salud para todos", "Construye un centro de salud accesible por carretera.", "HEALTH", 7, 30, null,
            listOf(RequirementSeed(RequirementType.PLACE_COUNT, "HEALTH_CENTER", 1))),
        MissionSeedDefinition("M08", "Cobertura médica", "Consigue que 3 casas tengan acceso al centro de salud.", "HEALTH", 8, 30, null,
            listOf(RequirementSeed(RequirementType.COVERAGE_COUNT, "HEALTH", 3))),
        MissionSeedDefinition("M09", "Un lugar para jugar", "Crea una plaza verde para el barrio.", "PARK", 9, 20, null,
            listOf(RequirementSeed(RequirementType.PLACE_COUNT, "PARK_SMALL", 1))),
        MissionSeedDefinition("M10", "Ciudad más verde", "Alcanza una puntuación verde de al menos 40.", "PARK", 10, 30, null,
            listOf(RequirementSeed(RequirementType.METRIC_THRESHOLD, "GREEN", 40))),
        MissionSeedDefinition("M11", "Agua para el barrio", "Construye una torre de agua conectada por carretera.", "WATER", 11, 30, null,
            listOf(RequirementSeed(RequirementType.PLACE_COUNT, "WATER_TOWER", 1))),
        MissionSeedDefinition("M12", "Grifo abierto", "Consigue que 3 casas tengan acceso al agua potable.", "WATER", 12, 30, "BADGE_WATER_HERO".let { null },
            listOf(RequirementSeed(RequirementType.COVERAGE_COUNT, "WATER", 3))),
        MissionSeedDefinition("M13", "Primer autobús", "Instala una parada de autobús en tu ciudad.", "TRANSPORT", 13, 25, null,
            listOf(RequirementSeed(RequirementType.PLACE_COUNT, "BUS_STOP", 1))),
        MissionSeedDefinition("M14", "Red bien pensada", "Construye 8 calles formando una sola red conectada.", "ROAD", 14, 35, null,
            listOf(RequirementSeed(RequirementType.ROAD_NETWORK_SIZE, "ANY", 8))),
        MissionSeedDefinition("M15", "Movilidad total", "Consigue que el 60% de tus casas tenga acceso a la calle.", "ROAD", 15, 35, null,
            listOf(RequirementSeed(RequirementType.METRIC_THRESHOLD, "MOBILITY", 60))),
        MissionSeedDefinition("M16", "Barrio educado", "Alcanza un 50% de cobertura educativa en la ciudad.", "EDUCATION", 16, 35, null,
            listOf(RequirementSeed(RequirementType.METRIC_THRESHOLD, "EDUCATION", 50))),
        MissionSeedDefinition("M17", "Barrio saludable", "Alcanza un 50% de cobertura sanitaria en la ciudad.", "HEALTH", 17, 35, null,
            listOf(RequirementSeed(RequirementType.METRIC_THRESHOLD, "HEALTH", 50))),
        MissionSeedDefinition("M18", "Segunda escuela", "Amplía la educación con una biblioteca.", "EDUCATION", 18, 25, null,
            listOf(RequirementSeed(RequirementType.PLACE_COUNT, "SCHOOL_LIBRARY", 1))),
        MissionSeedDefinition("M19", "Hospital de ciudad", "Construye un hospital para reforzar la salud.", "HEALTH", 19, 40, null,
            listOf(RequirementSeed(RequirementType.PLACE_COUNT, "HOSPITAL", 1))),
        MissionSeedDefinition("M20", "Parque mayor", "Construye un parque grande en tu ciudad.", "PARK", 20, 30, null,
            listOf(RequirementSeed(RequirementType.PLACE_COUNT, "PARK_LARGE", 1))),
        MissionSeedDefinition("M21", "Agua para todos", "Alcanza un 60% de cobertura de agua potable.", "WATER", 21, 35, "BADGE_WATER_HERO",
            listOf(RequirementSeed(RequirementType.METRIC_THRESHOLD, "WATER", 60))),
        MissionSeedDefinition("M22", "Estación de tren", "Conecta tu ciudad con una estación de tren.", "TRANSPORT", 22, 40, null,
            listOf(RequirementSeed(RequirementType.PLACE_COUNT, "TRAIN_STATION", 1))),
        MissionSeedDefinition("M23", "Presupuesto inteligente", "Consigue buenos servicios usando menos del 70% del presupuesto.", "GENERAL", 23, 40, "BADGE_BUDGET_WIZARD",
            listOf(RequirementSeed(RequirementType.BUDGET_EFFICIENCY, "ANY", 30), RequirementSeed(RequirementType.METRIC_THRESHOLD, "SERVICES", 60))),
        MissionSeedDefinition("M24", "Red ampliada", "Haz crecer tu red de calles hasta 15 casillas conectadas.", "ROAD", 24, 40, null,
            listOf(RequirementSeed(RequirementType.ROAD_NETWORK_SIZE, "ANY", 15))),
        MissionSeedDefinition("M25", "Ciudad conectada", "Alcanza un 80% de movilidad en toda la ciudad.", "ROAD", 25, 45, "BADGE_MOBILITY_MASTER",
            listOf(RequirementSeed(RequirementType.METRIC_THRESHOLD, "MOBILITY", 80))),
        MissionSeedDefinition("M26", "Barrio saludable avanzado", "Alcanza un 80% de cobertura sanitaria.", "HEALTH", 26, 45, "BADGE_HEALTH_HERO",
            listOf(RequirementSeed(RequirementType.METRIC_THRESHOLD, "HEALTH", 80))),
        MissionSeedDefinition("M27", "Barrio educado avanzado", "Alcanza un 80% de cobertura educativa.", "EDUCATION", 27, 45, "BADGE_EDUCATION_HERO",
            listOf(RequirementSeed(RequirementType.METRIC_THRESHOLD, "EDUCATION", 80))),
        MissionSeedDefinition("M28", "Ciudad muy verde", "Alcanza una puntuación verde de al menos 70.", "PARK", 28, 45, null,
            listOf(RequirementSeed(RequirementType.METRIC_THRESHOLD, "GREEN", 70))),
        MissionSeedDefinition("M29", "Todos los servicios", "Alcanza al menos 70 en la puntuación general de servicios.", "GENERAL", 29, 50, null,
            listOf(RequirementSeed(RequirementType.METRIC_THRESHOLD, "SERVICES", 70))),
        MissionSeedDefinition("M30", "Ciudad ejemplar", "Combina buena movilidad, servicios y áreas verdes en tu ciudad.", "GENERAL", 30, 60, null,
            listOf(
                RequirementSeed(RequirementType.METRIC_THRESHOLD, "MOBILITY", 85),
                RequirementSeed(RequirementType.METRIC_THRESHOLD, "SERVICES", 75),
                RequirementSeed(RequirementType.METRIC_THRESHOLD, "GREEN", 60)
            ))
    )
}
