package com.educalab.atumanera.data.seed

import com.educalab.atumanera.data.local.entity.BadgeEntity
import com.educalab.atumanera.data.local.entity.DecorationEntity
import com.educalab.atumanera.data.local.entity.InfrastructureTypeEntity
import com.educalab.atumanera.domain.model.InfraCategory

/**
 * Catálogo semilla de infraestructuras, insignias y decoraciones.
 * Se inserta una única vez al primer arranque para que la app instalada
 * se sienta completa desde el primer momento.
 */
object CatalogSeed {

    fun infrastructureTypes(): List<InfrastructureTypeEntity> = listOf(
        InfrastructureTypeEntity(0, "ROAD_BASIC", "Calle", InfraCategory.ROAD.name, "Conecta casillas y da acceso a toda la ciudad.", 10, 0, "ic_infra_road"),
        InfrastructureTypeEntity(0, "HOUSE_SMALL", "Casa pequeña", InfraCategory.HOUSING.name, "Vivienda para pocas familias.", 30, 0, "ic_infra_house_small"),
        InfrastructureTypeEntity(0, "HOUSE_BLOCK", "Bloque de viviendas", InfraCategory.HOUSING.name, "Vivienda de mayor densidad.", 70, 0, "ic_infra_house_block"),
        InfrastructureTypeEntity(0, "SCHOOL_PRIMARY", "Escuela primaria", InfraCategory.EDUCATION.name, "Da acceso educativo a las casas cercanas por carretera.", 120, 6, "ic_infra_school"),
        InfrastructureTypeEntity(0, "SCHOOL_LIBRARY", "Biblioteca", InfraCategory.EDUCATION.name, "Refuerza la cobertura educativa del barrio.", 90, 5, "ic_infra_library"),
        InfrastructureTypeEntity(0, "HEALTH_CENTER", "Centro de salud", InfraCategory.HEALTH.name, "Atiende a la población cercana conectada por carretera.", 100, 6, "ic_infra_health_center"),
        InfrastructureTypeEntity(0, "HOSPITAL", "Hospital", InfraCategory.HEALTH.name, "Mayor alcance de cobertura sanitaria.", 190, 8, "ic_infra_hospital"),
        InfrastructureTypeEntity(0, "PARK_SMALL", "Plaza verde", InfraCategory.PARK.name, "Área verde de barrio.", 35, 4, "ic_infra_park_small"),
        InfrastructureTypeEntity(0, "PARK_LARGE", "Parque grande", InfraCategory.PARK.name, "Gran área verde para toda la zona.", 80, 6, "ic_infra_park_large"),
        InfrastructureTypeEntity(0, "WATER_TOWER", "Torre de agua", InfraCategory.WATER.name, "Suministra agua a las casas conectadas.", 110, 7, "ic_infra_water_tower"),
        InfrastructureTypeEntity(0, "WATER_TREATMENT", "Planta potabilizadora", InfraCategory.WATER.name, "Amplía la cobertura de agua potable.", 160, 9, "ic_infra_water_plant"),
        InfrastructureTypeEntity(0, "BUS_STOP", "Parada de autobús", InfraCategory.TRANSPORT.name, "Transporte público de corto alcance.", 45, 5, "ic_infra_bus"),
        InfrastructureTypeEntity(0, "TRAIN_STATION", "Estación de tren", InfraCategory.TRANSPORT.name, "Transporte de largo alcance para la ciudad.", 150, 8, "ic_infra_train")
    )

    fun badges(): List<BadgeEntity> = listOf(
        BadgeEntity(0, "BADGE_FIRST_ROAD", "Primera Calle", "Construiste tu primera calle.", "ic_badge_road", "ROAD"),
        BadgeEntity(0, "BADGE_FIRST_HOUSE", "Primer Hogar", "Diste techo a tus primeros vecinos.", "ic_badge_house", "HOUSING"),
        BadgeEntity(0, "BADGE_FIRST_SCHOOL", "Primera Escuela", "Abriste la primera escuela de la ciudad.", "ic_badge_school", "EDUCATION"),
        BadgeEntity(0, "BADGE_MOBILITY_MASTER", "Maestro de la Movilidad", "El 90% de tus casas tiene acceso a la calle.", "ic_badge_mobility", "ROAD"),
        BadgeEntity(0, "BADGE_GREEN_CITY", "Ciudad Verde", "Lograste una puntuación verde de 80 o más.", "ic_badge_green", "PARK"),
        BadgeEntity(0, "BADGE_HEALTH_HERO", "Héroe de la Salud", "Cobertura sanitaria del 90% o más.", "ic_badge_health", "HEALTH"),
        BadgeEntity(0, "BADGE_WATER_HERO", "Héroe del Agua", "Cobertura de agua del 90% o más.", "ic_badge_water", "WATER"),
        BadgeEntity(0, "BADGE_EDUCATION_HERO", "Héroe de la Educación", "Cobertura educativa del 90% o más.", "ic_badge_education", "EDUCATION"),
        BadgeEntity(0, "BADGE_BUDGET_WIZARD", "Mago del Presupuesto", "Buenos servicios gastando poco presupuesto.", "ic_badge_budget", "GENERAL"),
        BadgeEntity(0, "BADGE_MISSION_10", "Explorador Urbano", "Completaste 10 misiones.", "ic_badge_missions10", "GENERAL"),
        BadgeEntity(0, "BADGE_MISSION_20", "Planificador Experto", "Completaste 20 misiones.", "ic_badge_missions20", "GENERAL"),
        BadgeEntity(0, "BADGE_XP_500", "Leyenda de la Ciudad", "Alcanzaste 500 puntos de experiencia.", "ic_badge_xp", "GENERAL"),
        BadgeEntity(0, "BADGE_LEVEL1_MASTER", "Fundamentos Dominados", "Completaste todas las misiones del Nivel 1.", "ic_badge_level1", "GENERAL"),
        BadgeEntity(0, "BADGE_LEVEL2_MASTER", "Maestro de Conexiones", "Completaste todas las misiones del Nivel 2 y desbloqueaste el Modo Libre.", "ic_badge_level2", "GENERAL"),
        BadgeEntity(0, "BADGE_LEVEL3_MASTER", "Solucionador Experto", "Completaste todas las misiones del Nivel 3.", "ic_badge_level3", "GENERAL"),
        BadgeEntity(0, "BADGE_GRAND_MASTER", "Alcalde de Mérito", "Completaste los 4 niveles de misiones: eres el mejor alcalde.", "ic_badge_grandmaster", "GENERAL"),
        BadgeEntity(0, "BADGE_TRANSPORT_MASTER", "Maestro del Transporte", "Construiste una gran red de autobuses y trenes.", "ic_badge_transport", "TRANSPORT"),
        BadgeEntity(0, "BADGE_MEGA_CITY", "Ciudad en Expansión", "Completaste 50 misiones.", "ic_badge_megacity", "GENERAL"),
        BadgeEntity(0, "BADGE_PERFECT_CITY", "Ciudad Perfecta", "Alcanzaste movilidad, servicios y verde altísimos a la vez.", "ic_badge_perfect", "GENERAL"),
        BadgeEntity(0, "BADGE_BUDGET_GENIUS", "Genio del Presupuesto", "Excelentes servicios gastando muy poco presupuesto.", "ic_badge_geniusbudget", "GENERAL")
    )

    fun decorations(): List<DecorationEntity> = listOf(
        DecorationEntity(0, "DECO_FOUNTAIN", "Fuente Central", "Una fuente decorativa para la plaza principal.", "ic_deco_fountain", "DECO_FOUNTAIN"),
        DecorationEntity(0, "DECO_CLOCK_TOWER", "Torre del Reloj", "Un símbolo de una ciudad bien conectada.", "ic_deco_clock", "DECO_CLOCK_TOWER"),
        DecorationEntity(0, "DECO_STATUE", "Estatua del Fundador", "Homenaje a los primeros pasos de tu ciudad.", "ic_deco_statue", "DECO_STATUE"),
        DecorationEntity(0, "DECO_GARDEN", "Jardín Botánico", "Un jardín exuberante para una ciudad muy verde.", "ic_deco_garden", "DECO_GARDEN"),
        DecorationEntity(0, "DECO_BRIDGE", "Puente Panorámico", "Une los barrios más lejanos de la ciudad.", "ic_deco_bridge", "DECO_BRIDGE"),
        DecorationEntity(0, "DECO_OBELISK", "Obelisco Conmemorativo", "Marca los grandes logros de tu ciudad.", "ic_deco_obelisk", "DECO_OBELISK"),
        DecorationEntity(0, "DECO_BANDSTAND", "Quiosco de Música", "Punto de encuentro para una ciudad con buenos servicios.", "ic_deco_bandstand", "DECO_BANDSTAND"),
        DecorationEntity(0, "DECO_LIGHTHOUSE", "Faro del Puerto", "Homenaje a una red de agua impecable.", "ic_deco_lighthouse", "DECO_LIGHTHOUSE")
    )
}
