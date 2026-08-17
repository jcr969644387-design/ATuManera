package com.educalab.atumanera.domain.logic

import com.educalab.atumanera.domain.model.CityMetricsSnapshot
import com.educalab.atumanera.domain.model.InfraCategory
import com.educalab.atumanera.domain.model.TileSnapshot

/**
 * Deriva los indicadores globales de la ciudad (movilidad, servicios, áreas
 * verdes, cobertura educativa/salud/agua y presupuesto) a partir del estado
 * real de la cuadrícula. Ningún valor se inventa: todo proviene de [GridEngine].
 */
class MetricsCalculator(private val gridEngine: GridEngine) {

    fun calculate(
        tiles: List<TileSnapshot>,
        budgetSpent: Int,
        budgetTotal: Int
    ): CityMetricsSnapshot {
        val mobility = gridEngine.mobilityPercent(tiles)

        val educationCoverage = gridEngine.computeCoverage(tiles, InfraCategory.EDUCATION).coveragePercent
        val healthCoverage = gridEngine.computeCoverage(tiles, InfraCategory.HEALTH).coveragePercent
        val waterCoverage = gridEngine.computeCoverage(tiles, InfraCategory.WATER).coveragePercent
        val parkCoverage = gridEngine.computeCoverage(tiles, InfraCategory.PARK).coveragePercent

        val servicesScore = listOf(educationCoverage, healthCoverage, waterCoverage)
            .let { if (it.isEmpty()) 0 else it.sum() / it.size }

        val houseCount = tiles.count { it.category == InfraCategory.HOUSING }
        val parkCount = tiles.count { it.category == InfraCategory.PARK }
        // Puntuación verde: combina cobertura real de parques con una densidad razonable
        // (1 parque por cada 4 viviendas se considera una cobertura completa de densidad).
        val densityScore = if (houseCount == 0) 0 else (((parkCount * 4).coerceAtMost(houseCount) * 100) / houseCount)
        val greenScore = ((parkCoverage + densityScore) / 2).coerceIn(0, 100)

        return CityMetricsSnapshot(
            mobility = mobility,
            servicesScore = servicesScore,
            greenScore = greenScore,
            educationCoverage = educationCoverage,
            healthCoverage = healthCoverage,
            waterCoverage = waterCoverage,
            budgetSpent = budgetSpent,
            budgetTotal = budgetTotal
        )
    }
}
