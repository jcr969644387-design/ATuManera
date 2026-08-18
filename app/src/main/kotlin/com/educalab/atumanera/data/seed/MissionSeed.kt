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
 * Misiones que guían el progreso del jugador en 4 niveles de dificultad
 * creciente: Nivel 1 (1-30, "¿qué construir?"), Nivel 2 (31-48, "¿dónde
 * construir?"), Nivel 3 (49-59, "¿qué problema solucionar?") y Nivel 4
 * (60-67, "¿qué decisión es mejor y por qué?"). Completar cada nivel por
 * completo libera presupuesto adicional (ver [com.educalab.atumanera.data.repository.CityRepository]).
 * Todas se evalúan con [com.educalab.atumanera.domain.logic.MissionEvaluator]
 * contra datos reales.
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
            )),

        // ---------- Nivel 2 (31-48): "¿Dónde debo construir?" — relacionar módulos entre sí ----------
        MissionSeedDefinition("M31", "Barrio conectado", "Construye 5 casas y una red de al menos 12 calles unidas entre sí.", "ROAD", 31, 55, null,
            listOf(RequirementSeed(RequirementType.PLACE_COUNT, "HOUSE_SMALL", 5), RequirementSeed(RequirementType.ROAD_NETWORK_SIZE, "ANY", 12))),
        MissionSeedDefinition("M32", "Todos a clase", "Consigue que 6 casas tengan acceso a la escuela.", "EDUCATION", 32, 55, null,
            listOf(RequirementSeed(RequirementType.COVERAGE_COUNT, "EDUCATION", 6))),
        MissionSeedDefinition("M33", "Doble atención médica", "Consigue que 6 casas tengan acceso a salud.", "HEALTH", 33, 55, null,
            listOf(RequirementSeed(RequirementType.COVERAGE_COUNT, "HEALTH", 6))),
        MissionSeedDefinition("M34", "Agua para el barrio nuevo", "Consigue que 6 casas tengan acceso al agua potable.", "WATER", 34, 55, null,
            listOf(RequirementSeed(RequirementType.COVERAGE_COUNT, "WATER", 6))),
        MissionSeedDefinition("M35", "Parques por todas partes", "Consigue que 6 casas tengan un parque cerca.", "PARK", 35, 55, null,
            listOf(RequirementSeed(RequirementType.COVERAGE_COUNT, "PARK", 6))),
        MissionSeedDefinition("M36", "Ciudad más móvil", "Alcanza un 45% de movilidad.", "ROAD", 36, 55, null,
            listOf(RequirementSeed(RequirementType.METRIC_THRESHOLD, "MOBILITY", 45))),
        MissionSeedDefinition("M37", "Servicios en marcha", "Alcanza un 40% en la puntuación de servicios.", "GENERAL", 37, 55, null,
            listOf(RequirementSeed(RequirementType.METRIC_THRESHOLD, "SERVICES", 40))),
        MissionSeedDefinition("M38", "Balance verde", "Alcanza una puntuación verde de al menos 45.", "PARK", 38, 55, null,
            listOf(RequirementSeed(RequirementType.METRIC_THRESHOLD, "GREEN", 45))),
        MissionSeedDefinition("M39", "Dos escuelas activas", "Construye una escuela y una biblioteca, y llega a 35% de cobertura educativa.", "EDUCATION", 39, 60, null,
            listOf(
                RequirementSeed(RequirementType.PLACE_COUNT, "SCHOOL_PRIMARY", 1),
                RequirementSeed(RequirementType.PLACE_COUNT, "SCHOOL_LIBRARY", 1),
                RequirementSeed(RequirementType.METRIC_THRESHOLD, "EDUCATION", 35)
            )),
        MissionSeedDefinition("M40", "Salud completa", "Construye un centro de salud y un hospital, y llega a 35% de cobertura sanitaria.", "HEALTH", 40, 60, null,
            listOf(
                RequirementSeed(RequirementType.PLACE_COUNT, "HEALTH_CENTER", 1),
                RequirementSeed(RequirementType.PLACE_COUNT, "HOSPITAL", 1),
                RequirementSeed(RequirementType.METRIC_THRESHOLD, "HEALTH", 35)
            )),
        MissionSeedDefinition("M41", "Doble suministro de agua", "Construye una torre de agua y una planta potabilizadora, y llega a 35% de cobertura de agua.", "WATER", 41, 60, null,
            listOf(
                RequirementSeed(RequirementType.PLACE_COUNT, "WATER_TOWER", 1),
                RequirementSeed(RequirementType.PLACE_COUNT, "WATER_TREATMENT", 1),
                RequirementSeed(RequirementType.METRIC_THRESHOLD, "WATER", 35)
            )),
        MissionSeedDefinition("M42", "Transporte combinado", "Instala 2 paradas de autobús y 1 estación de tren.", "TRANSPORT", 42, 55, null,
            listOf(RequirementSeed(RequirementType.PLACE_COUNT, "BUS_STOP", 2), RequirementSeed(RequirementType.PLACE_COUNT, "TRAIN_STATION", 1))),
        MissionSeedDefinition("M43", "Vecindario de bloques", "Construye 3 bloques de viviendas y alcanza 40% de movilidad.", "HOUSING", 43, 55, null,
            listOf(RequirementSeed(RequirementType.PLACE_COUNT, "HOUSE_BLOCK", 3), RequirementSeed(RequirementType.METRIC_THRESHOLD, "MOBILITY", 40))),
        MissionSeedDefinition("M44", "Red de 20 calles", "Haz crecer tu red de calles conectadas hasta 20 casillas.", "ROAD", 44, 60, null,
            listOf(RequirementSeed(RequirementType.ROAD_NETWORK_SIZE, "ANY", 20))),
        MissionSeedDefinition("M45", "Barrio en expansión", "Combina bloques y casas pequeñas, y alcanza 50% de movilidad.", "HOUSING", 45, 60, null,
            listOf(
                RequirementSeed(RequirementType.PLACE_COUNT, "HOUSE_BLOCK", 2),
                RequirementSeed(RequirementType.PLACE_COUNT, "HOUSE_SMALL", 4),
                RequirementSeed(RequirementType.METRIC_THRESHOLD, "MOBILITY", 50)
            )),
        MissionSeedDefinition("M46", "Ahorro inteligente", "Alcanza 35% de servicios usando como máximo 60% del presupuesto.", "GENERAL", 46, 60, null,
            listOf(RequirementSeed(RequirementType.BUDGET_EFFICIENCY, "ANY", 40), RequirementSeed(RequirementType.METRIC_THRESHOLD, "SERVICES", 35))),
        MissionSeedDefinition("M47", "Parques y casas en equilibrio", "Construye 2 plazas y 1 parque grande, y llega a 55 de puntuación verde.", "PARK", 47, 60, null,
            listOf(
                RequirementSeed(RequirementType.PLACE_COUNT, "PARK_SMALL", 2),
                RequirementSeed(RequirementType.PLACE_COUNT, "PARK_LARGE", 1),
                RequirementSeed(RequirementType.METRIC_THRESHOLD, "GREEN", 55)
            )),
        MissionSeedDefinition("M48", "Ciudad de nivel medio", "Alcanza 50% de movilidad, 45% de servicios y 45 de puntuación verde a la vez.", "GENERAL", 48, 70, null,
            listOf(
                RequirementSeed(RequirementType.METRIC_THRESHOLD, "MOBILITY", 50),
                RequirementSeed(RequirementType.METRIC_THRESHOLD, "SERVICES", 45),
                RequirementSeed(RequirementType.METRIC_THRESHOLD, "GREEN", 45)
            )),

        // ---------- Nivel 3 (49-59): "¿Qué problema debo solucionar?" — optimizar con recursos limitados ----------
        MissionSeedDefinition("M49", "Optimiza tus calles", "Construye una red de 30 calles conectadas gastando como máximo 65% del presupuesto.", "ROAD", 49, 75, null,
            listOf(RequirementSeed(RequirementType.ROAD_NETWORK_SIZE, "ANY", 30), RequirementSeed(RequirementType.BUDGET_EFFICIENCY, "ANY", 35))),
        MissionSeedDefinition("M50", "Cobertura educativa alta", "Alcanza 65% de cobertura educativa con al menos 10 casas cubiertas.", "EDUCATION", 50, 75, null,
            listOf(RequirementSeed(RequirementType.METRIC_THRESHOLD, "EDUCATION", 65), RequirementSeed(RequirementType.COVERAGE_COUNT, "EDUCATION", 10))),
        MissionSeedDefinition("M51", "Cobertura sanitaria alta", "Alcanza 65% de cobertura sanitaria con al menos 10 casas cubiertas.", "HEALTH", 51, 75, null,
            listOf(RequirementSeed(RequirementType.METRIC_THRESHOLD, "HEALTH", 65), RequirementSeed(RequirementType.COVERAGE_COUNT, "HEALTH", 10))),
        MissionSeedDefinition("M52", "Agua para toda la ciudad", "Alcanza 65% de cobertura de agua con al menos 10 casas cubiertas.", "WATER", 52, 75, null,
            listOf(RequirementSeed(RequirementType.METRIC_THRESHOLD, "WATER", 65), RequirementSeed(RequirementType.COVERAGE_COUNT, "WATER", 10))),
        MissionSeedDefinition("M53", "Ciudad eficiente", "Alcanza 55% de servicios gastando como máximo 55% del presupuesto.", "GENERAL", 53, 80, null,
            listOf(RequirementSeed(RequirementType.BUDGET_EFFICIENCY, "ANY", 45), RequirementSeed(RequirementType.METRIC_THRESHOLD, "SERVICES", 55))),
        MissionSeedDefinition("M54", "Movilidad casi total", "Alcanza 75% de movilidad con una red de al menos 25 calles.", "ROAD", 54, 80, null,
            listOf(RequirementSeed(RequirementType.METRIC_THRESHOLD, "MOBILITY", 75), RequirementSeed(RequirementType.ROAD_NETWORK_SIZE, "ANY", 25))),
        MissionSeedDefinition("M55", "Ciudad verde avanzada", "Construye 2 parques grandes y alcanza 65 de puntuación verde.", "PARK", 55, 80, null,
            listOf(RequirementSeed(RequirementType.PLACE_COUNT, "PARK_LARGE", 2), RequirementSeed(RequirementType.METRIC_THRESHOLD, "GREEN", 65))),
        MissionSeedDefinition("M56", "Doble transporte eficiente", "Instala 3 paradas de autobús y 2 estaciones de tren, y llega a 70% de movilidad.", "TRANSPORT", 56, 80, null,
            listOf(
                RequirementSeed(RequirementType.PLACE_COUNT, "BUS_STOP", 3),
                RequirementSeed(RequirementType.PLACE_COUNT, "TRAIN_STATION", 2),
                RequirementSeed(RequirementType.METRIC_THRESHOLD, "MOBILITY", 70)
            )),
        MissionSeedDefinition("M57", "Gran barrio residencial", "Construye 5 bloques de viviendas y alcanza 70% de movilidad y 50% de servicios.", "HOUSING", 57, 85, null,
            listOf(
                RequirementSeed(RequirementType.PLACE_COUNT, "HOUSE_BLOCK", 5),
                RequirementSeed(RequirementType.METRIC_THRESHOLD, "MOBILITY", 70),
                RequirementSeed(RequirementType.METRIC_THRESHOLD, "SERVICES", 50)
            )),
        MissionSeedDefinition("M58", "Todo bajo control", "Alcanza 70% de movilidad y 60% de servicios gastando como máximo 70% del presupuesto.", "GENERAL", 58, 85, null,
            listOf(
                RequirementSeed(RequirementType.METRIC_THRESHOLD, "MOBILITY", 70),
                RequirementSeed(RequirementType.METRIC_THRESHOLD, "SERVICES", 60),
                RequirementSeed(RequirementType.BUDGET_EFFICIENCY, "ANY", 30)
            )),
        MissionSeedDefinition("M59", "El gran reto de nivel 3", "Combina 75% de movilidad, 65% de servicios y 60 de puntuación verde gastando como máximo 75% del presupuesto.", "GENERAL", 59, 100, null,
            listOf(
                RequirementSeed(RequirementType.METRIC_THRESHOLD, "MOBILITY", 75),
                RequirementSeed(RequirementType.METRIC_THRESHOLD, "SERVICES", 65),
                RequirementSeed(RequirementType.METRIC_THRESHOLD, "GREEN", 60),
                RequirementSeed(RequirementType.BUDGET_EFFICIENCY, "ANY", 25)
            )),

        // ---------- Nivel 4 (60-67): "¿Qué decisión es mejor y por qué?" — dominio total, decisiones con consecuencias ----------
        MissionSeedDefinition("M60", "Ciudad ejemplar avanzada", "Combina 90% de movilidad, 80% de servicios y 75 de puntuación verde.", "GENERAL", 60, 110, null,
            listOf(
                RequirementSeed(RequirementType.METRIC_THRESHOLD, "MOBILITY", 90),
                RequirementSeed(RequirementType.METRIC_THRESHOLD, "SERVICES", 80),
                RequirementSeed(RequirementType.METRIC_THRESHOLD, "GREEN", 75)
            )),
        MissionSeedDefinition("M61", "Maestría educativa", "Alcanza 90% de cobertura educativa con al menos 18 casas cubiertas.", "EDUCATION", 61, 110, null,
            listOf(RequirementSeed(RequirementType.METRIC_THRESHOLD, "EDUCATION", 90), RequirementSeed(RequirementType.COVERAGE_COUNT, "EDUCATION", 18))),
        MissionSeedDefinition("M62", "Maestría sanitaria", "Alcanza 90% de cobertura sanitaria con al menos 18 casas cubiertas.", "HEALTH", 62, 110, null,
            listOf(RequirementSeed(RequirementType.METRIC_THRESHOLD, "HEALTH", 90), RequirementSeed(RequirementType.COVERAGE_COUNT, "HEALTH", 18))),
        MissionSeedDefinition("M63", "Maestría hídrica", "Alcanza 90% de cobertura de agua con al menos 18 casas cubiertas.", "WATER", 63, 110, null,
            listOf(RequirementSeed(RequirementType.METRIC_THRESHOLD, "WATER", 90), RequirementSeed(RequirementType.COVERAGE_COUNT, "WATER", 18))),
        MissionSeedDefinition("M64", "Presupuesto maestro", "Alcanza 80% de movilidad y 75% de servicios gastando como máximo 45% del presupuesto total.", "GENERAL", 64, 120, "BADGE_BUDGET_WIZARD",
            listOf(
                RequirementSeed(RequirementType.BUDGET_EFFICIENCY, "ANY", 55),
                RequirementSeed(RequirementType.METRIC_THRESHOLD, "SERVICES", 75),
                RequirementSeed(RequirementType.METRIC_THRESHOLD, "MOBILITY", 80)
            )),
        MissionSeedDefinition("M65", "Red vial perfecta", "Construye una red de 45 calles conectadas y alcanza 95% de movilidad.", "ROAD", 65, 120, null,
            listOf(RequirementSeed(RequirementType.ROAD_NETWORK_SIZE, "ANY", 45), RequirementSeed(RequirementType.METRIC_THRESHOLD, "MOBILITY", 95))),
        MissionSeedDefinition("M66", "Metrópoli verde", "Construye 3 parques grandes y 3 plazas, y alcanza 85 de puntuación verde.", "PARK", 66, 120, "BADGE_GREEN_CITY",
            listOf(
                RequirementSeed(RequirementType.PLACE_COUNT, "PARK_LARGE", 3),
                RequirementSeed(RequirementType.PLACE_COUNT, "PARK_SMALL", 3),
                RequirementSeed(RequirementType.METRIC_THRESHOLD, "GREEN", 85)
            )),
        MissionSeedDefinition("M67", "La ciudad perfecta", "El reto final: 95% de movilidad, 90% de servicios y 85 de puntuación verde gastando como máximo 60% del presupuesto.", "GENERAL", 67, 150, null,
            listOf(
                RequirementSeed(RequirementType.METRIC_THRESHOLD, "MOBILITY", 95),
                RequirementSeed(RequirementType.METRIC_THRESHOLD, "SERVICES", 90),
                RequirementSeed(RequirementType.METRIC_THRESHOLD, "GREEN", 85),
                RequirementSeed(RequirementType.BUDGET_EFFICIENCY, "ANY", 40)
            ))
    )
}
