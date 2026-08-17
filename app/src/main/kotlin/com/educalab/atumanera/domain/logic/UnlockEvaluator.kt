package com.educalab.atumanera.domain.logic

import com.educalab.atumanera.domain.model.CityMetricsSnapshot

/** Definición de una condición de desbloqueo de insignia o decoración. */
data class UnlockCondition(
    val code: String,
    val predicate: (UnlockContext) -> Boolean
)

/** Contexto con todo lo necesario para evaluar condiciones de desbloqueo. */
data class UnlockContext(
    val totalXp: Int,
    val missionsCompleted: Int,
    val metrics: CityMetricsSnapshot,
    val placedCounts: Map<String, Int>
)

/**
 * Evalúa qué insignias y decoraciones deberían estar desbloqueadas dado el
 * progreso real acumulado. Las condiciones son explícitas y comprobables,
 * nunca aleatorias.
 */
class UnlockEvaluator {

    fun evaluateBadges(context: UnlockContext, conditions: List<UnlockCondition>): List<String> =
        conditions.filter { it.predicate(context) }.map { it.code }

    fun evaluateDecorations(context: UnlockContext, conditions: List<UnlockCondition>): List<String> =
        conditions.filter { it.predicate(context) }.map { it.code }

    companion object {
        /** Catálogo de condiciones estándar de insignias (usadas por el seeder y los tests). */
        fun defaultBadgeConditions(): List<UnlockCondition> = listOf(
            UnlockCondition("BADGE_FIRST_ROAD") { it.placedCounts["ROAD_BASIC"] ?: 0 >= 1 },
            UnlockCondition("BADGE_FIRST_HOUSE") { (it.placedCounts["HOUSE_SMALL"] ?: 0) + (it.placedCounts["HOUSE_BLOCK"] ?: 0) >= 1 },
            UnlockCondition("BADGE_FIRST_SCHOOL") { it.placedCounts["SCHOOL_PRIMARY"] ?: 0 >= 1 },
            UnlockCondition("BADGE_MOBILITY_MASTER") { it.metrics.mobility >= 90 },
            UnlockCondition("BADGE_GREEN_CITY") { it.metrics.greenScore >= 80 },
            UnlockCondition("BADGE_HEALTH_HERO") { it.metrics.healthCoverage >= 90 },
            UnlockCondition("BADGE_WATER_HERO") { it.metrics.waterCoverage >= 90 },
            UnlockCondition("BADGE_EDUCATION_HERO") { it.metrics.educationCoverage >= 90 },
            UnlockCondition("BADGE_BUDGET_WIZARD") { it.metrics.budgetUsedPercent in 1..70 && it.metrics.servicesScore >= 60 },
            UnlockCondition("BADGE_MISSION_10") { it.missionsCompleted >= 10 },
            UnlockCondition("BADGE_MISSION_20") { it.missionsCompleted >= 20 },
            UnlockCondition("BADGE_XP_500") { it.totalXp >= 500 }
        )

        /** Catálogo de condiciones estándar de monumentos decorativos. */
        fun defaultDecorationConditions(): List<UnlockCondition> = listOf(
            UnlockCondition("DECO_FOUNTAIN") { it.metrics.greenScore >= 40 },
            UnlockCondition("DECO_CLOCK_TOWER") { it.metrics.mobility >= 60 },
            UnlockCondition("DECO_STATUE") { it.missionsCompleted >= 5 },
            UnlockCondition("DECO_GARDEN") { it.metrics.greenScore >= 70 },
            UnlockCondition("DECO_BRIDGE") { it.metrics.mobility >= 85 },
            UnlockCondition("DECO_OBELISK") { it.totalXp >= 300 },
            UnlockCondition("DECO_BANDSTAND") { it.metrics.servicesScore >= 75 },
            UnlockCondition("DECO_LIGHTHOUSE") { it.metrics.waterCoverage >= 95 }
        )
    }
}
