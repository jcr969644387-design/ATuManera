package com.educalab.atumanera.domain.logic

import com.educalab.atumanera.domain.model.CityMetricsSnapshot
import com.educalab.atumanera.domain.model.MissionEvaluation
import com.educalab.atumanera.domain.model.RequirementType

/** Un requisito individual de una misión (fila flexible, sin lógica hardcodeada por misión). */
data class MissionRequirementInput(
    val type: RequirementType,
    val key: String,
    val targetValue: Int
)

/** Estado real y completo de una ciudad que se compara contra los requisitos de misión. */
data class CityStateSnapshot(
    val placedCounts: Map<String, Int>,
    val metrics: CityMetricsSnapshot,
    val largestRoadComponent: Int,
    val coverageCounts: Map<String, Int>
)

/**
 * Evalúa si una misión está completa comparando sus requisitos reales contra
 * el estado actual y persistido de la ciudad. No usa valores aleatorios ni
 * simulados: todo proviene de las tablas de infraestructura y métricas reales.
 */
class MissionEvaluator {

    fun evaluate(missionId: Long, requirements: List<MissionRequirementInput>, state: CityStateSnapshot): MissionEvaluation {
        if (requirements.isEmpty()) return MissionEvaluation(missionId, false, 0)

        var totalProgress = 0.0
        var allComplete = true

        for (req in requirements) {
            val currentValue = currentValueFor(req, state)
            val fraction = if (req.targetValue <= 0) 1.0
            else (currentValue.toDouble() / req.targetValue.toDouble()).coerceIn(0.0, 1.0)
            totalProgress += fraction
            if (currentValue < req.targetValue) allComplete = false
        }

        val progressPercent = ((totalProgress / requirements.size) * 100).toInt().coerceIn(0, 100)
        return MissionEvaluation(missionId, allComplete, progressPercent)
    }

    private fun currentValueFor(req: MissionRequirementInput, state: CityStateSnapshot): Int {
        return when (req.type) {
            RequirementType.PLACE_COUNT -> state.placedCounts[req.key] ?: 0
            RequirementType.METRIC_THRESHOLD -> metricValue(req.key, state.metrics)
            RequirementType.ROAD_NETWORK_SIZE -> state.largestRoadComponent
            RequirementType.COVERAGE_COUNT -> state.coverageCounts[req.key] ?: 0
            RequirementType.BUDGET_EFFICIENCY -> {
                // Cuanto menor el % de presupuesto usado, mayor el "valor" de eficiencia (0-100).
                (100 - state.metrics.budgetUsedPercent).coerceIn(0, 100)
            }
        }
    }

    private fun metricValue(key: String, metrics: CityMetricsSnapshot): Int = when (key) {
        "MOBILITY" -> metrics.mobility
        "SERVICES" -> metrics.servicesScore
        "GREEN" -> metrics.greenScore
        "EDUCATION" -> metrics.educationCoverage
        "HEALTH" -> metrics.healthCoverage
        "WATER" -> metrics.waterCoverage
        else -> 0
    }
}
