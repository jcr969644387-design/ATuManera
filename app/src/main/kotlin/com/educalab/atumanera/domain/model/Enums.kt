package com.educalab.atumanera.domain.model

/**
 * Categoría funcional de una infraestructura colocable en la cuadrícula.
 * Cada categoría corresponde a uno de los módulos de construcción de la app.
 */
enum class InfraCategory {
    ROAD,
    HOUSING,
    EDUCATION,
    HEALTH,
    PARK,
    WATER,
    TRANSPORT
}

/** Estado visual/funcional de un módulo dentro del mapa de progresión. */
enum class ModuleState {
    LOCKED,
    AVAILABLE,
    STARTED,
    COMPLETED,
    MASTERED
}

/** Estado de una misión para el usuario actual. */
enum class MissionStatus {
    LOCKED,
    AVAILABLE,
    IN_PROGRESS,
    COMPLETED
}

/** Tipos de requisito que puede tener una misión (fila flexible MissionRequirement). */
enum class RequirementType {
    /** Colocar N unidades de una infraestructura concreta (requirementKey = código infra). */
    PLACE_COUNT,
    /** Alcanzar un umbral en una métrica de la ciudad (requirementKey = nombre métrica 0-100). */
    METRIC_THRESHOLD,
    /** Alcanzar un tamaño mínimo de componente de carretera conectada. */
    ROAD_NETWORK_SIZE,
    /** Gastar como máximo cierta cantidad del presupuesto total asignado. */
    BUDGET_EFFICIENCY,
    /** Tener al menos N tiles cubiertos por un servicio concreto. */
    COVERAGE_COUNT
}

/** Categoría de una métrica de la ciudad, usada en gráficos e indicadores. */
enum class MetricType {
    MOBILITY,
    SERVICES,
    GREEN_AREAS,
    EDUCATION_COVERAGE,
    HEALTH_COVERAGE,
    WATER_COVERAGE,
    BUDGET_REMAINING
}
