package com.educalab.atumanera.domain.logic

import com.educalab.atumanera.domain.model.GridPosition
import com.educalab.atumanera.domain.model.InfraCategory
import com.educalab.atumanera.domain.model.TileSnapshot
import org.junit.Assert.assertEquals
import org.junit.Test

class MetricsCalculatorTest {

    private val engine = GridEngine(rows = 10, cols = 10)
    private val calculator = MetricsCalculator(engine)

    private fun road(row: Int, col: Int) = TileSnapshot(GridPosition(row, col), "ROAD_BASIC", InfraCategory.ROAD)
    private fun house(row: Int, col: Int) = TileSnapshot(GridPosition(row, col), "HOUSE_SMALL", InfraCategory.HOUSING)
    private fun park(row: Int, col: Int, radius: Int = 4) = TileSnapshot(GridPosition(row, col), "PARK_SMALL", InfraCategory.PARK, radius)

    @Test
    fun `empty city has all metrics at zero`() {
        val metrics = calculator.calculate(emptyList(), budgetSpent = 0, budgetTotal = 1000)
        assertEquals(0, metrics.mobility)
        assertEquals(0, metrics.servicesScore)
        assertEquals(0, metrics.greenScore)
        assertEquals(1000, metrics.budgetRemaining)
    }

    @Test
    fun `mobility reflects grid engine calculation`() {
        val tiles = listOf(road(0, 0), house(0, 1))
        val metrics = calculator.calculate(tiles, 0, 1000)
        assertEquals(100, metrics.mobility)
    }

    @Test
    fun `green score combines park coverage and planting density`() {
        val tiles = listOf(
            road(0, 0), road(0, 1), road(0, 2), road(0, 3), road(0, 4),
            house(1, 0), house(1, 1), house(1, 2), house(1, 3),
            park(1, 4, radius = 10)
        )
        val metrics = calculator.calculate(tiles, 0, 1000)
        // 4 casas conectadas, 1 parque cubre a todas (100% cobertura) y densidad 1 parque/4 casas (100%).
        assertEquals(100, metrics.greenScore)
    }

    @Test
    fun `budget spent and total are preserved in the snapshot`() {
        val metrics = calculator.calculate(emptyList(), budgetSpent = 300, budgetTotal = 1000)
        assertEquals(300, metrics.budgetSpent)
        assertEquals(1000, metrics.budgetTotal)
        assertEquals(700, metrics.budgetRemaining)
        assertEquals(30, metrics.budgetUsedPercent)
    }

    @Test
    fun `budget remaining never goes below zero even if overspent`() {
        val metrics = calculator.calculate(emptyList(), budgetSpent = 1200, budgetTotal = 1000)
        assertEquals(0, metrics.budgetRemaining)
    }

    @Test
    fun `services score averages education health and water coverage`() {
        val tiles = listOf(
            road(0, 0), road(0, 1),
            house(1, 0),
            TileSnapshot(GridPosition(1, 1), "SCHOOL_PRIMARY", InfraCategory.EDUCATION, 6)
        )
        val metrics = calculator.calculate(tiles, 0, 1000)
        // Sólo educación tiene cobertura (100%); salud y agua están a 0 -> promedio = 100/3 = 33.
        assertEquals((100 + 0 + 0) / 3, metrics.servicesScore)
    }
}
