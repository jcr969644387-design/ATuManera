package com.educalab.atumanera.ui.components

import com.educalab.atumanera.data.local.entity.CityMetricEntity
import com.educalab.atumanera.domain.model.InfraCategory
import com.educalab.atumanera.domain.model.ModuleState
import com.educalab.atumanera.ui.TileVisual

/**
 * Deriva el estado visual de un módulo (bloqueado/disponible/iniciado/completado/dominado)
 * a partir de las construcciones reales y las métricas persistidas. Nunca es aleatorio.
 */
fun moduleStateFor(category: InfraCategory, tiles: List<TileVisual>, latestMetric: CityMetricEntity?): ModuleState {
    val count = tiles.count { it.infraType?.category == category.name }
    if (count == 0) return ModuleState.AVAILABLE
    val metric = latestMetric

    return when (category) {
        InfraCategory.ROAD -> scoreToState(metric?.mobility ?: 0)
        InfraCategory.HOUSING -> if (count >= 6) ModuleState.COMPLETED else ModuleState.STARTED
        InfraCategory.EDUCATION -> scoreToState(metric?.educationCoverage ?: 0)
        InfraCategory.HEALTH -> scoreToState(metric?.healthCoverage ?: 0)
        InfraCategory.PARK -> scoreToState(metric?.greenScore ?: 0)
        InfraCategory.WATER -> scoreToState(metric?.waterCoverage ?: 0)
        InfraCategory.TRANSPORT -> if (count >= 2) ModuleState.COMPLETED else ModuleState.STARTED
    }
}

private fun scoreToState(score: Int): ModuleState = when {
    score >= 90 -> ModuleState.MASTERED
    score >= 50 -> ModuleState.COMPLETED
    score > 0 -> ModuleState.STARTED
    else -> ModuleState.STARTED
}
