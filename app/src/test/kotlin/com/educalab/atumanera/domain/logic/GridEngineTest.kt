package com.educalab.atumanera.domain.logic

import com.educalab.atumanera.domain.model.GridPosition
import com.educalab.atumanera.domain.model.InfraCategory
import com.educalab.atumanera.domain.model.TileSnapshot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GridEngineTest {

    private val engine = GridEngine(rows = 10, cols = 10)

    private fun road(row: Int, col: Int) = TileSnapshot(GridPosition(row, col), "ROAD_BASIC", InfraCategory.ROAD)
    private fun house(row: Int, col: Int) = TileSnapshot(GridPosition(row, col), "HOUSE_SMALL", InfraCategory.HOUSING)
    private fun school(row: Int, col: Int, radius: Int = 6) = TileSnapshot(GridPosition(row, col), "SCHOOL_PRIMARY", InfraCategory.EDUCATION, radius)

    @Test
    fun `empty grid has no road components`() {
        val result = engine.computeRoadNetwork(emptyList())
        assertTrue(result.componentSizes.isEmpty())
    }

    @Test
    fun `single road tile forms a component of size one`() {
        val result = engine.computeRoadNetwork(listOf(road(2, 2)))
        assertEquals(1, result.componentSizeAt(GridPosition(2, 2)))
    }

    @Test
    fun `adjacent road tiles form a single connected component`() {
        val tiles = listOf(road(0, 0), road(0, 1), road(0, 2), road(1, 2))
        val result = engine.computeRoadNetwork(tiles)
        val componentIds = tiles.map { result.componentOf[it.position] }.toSet()
        assertEquals(1, componentIds.size)
        assertEquals(4, result.largestComponentSize())
    }

    @Test
    fun `disconnected road segments form separate components`() {
        val tiles = listOf(road(0, 0), road(0, 1), road(5, 5), road(5, 6))
        val result = engine.computeRoadNetwork(tiles)
        assertEquals(2, result.componentSizes.size)
        assertEquals(2, result.componentSizeAt(GridPosition(0, 0)))
        assertEquals(2, result.componentSizeAt(GridPosition(5, 5)))
    }

    @Test
    fun `diagonal road tiles are not connected`() {
        val tiles = listOf(road(0, 0), road(1, 1))
        val result = engine.computeRoadNetwork(tiles)
        assertEquals(2, result.componentSizes.size)
    }

    @Test
    fun `mobility percent is zero without houses`() {
        val tiles = listOf(road(0, 0))
        assertEquals(0, engine.mobilityPercent(tiles))
    }

    @Test
    fun `mobility percent counts only houses adjacent to a road`() {
        val tiles = listOf(road(0, 0), house(0, 1), house(5, 5))
        assertEquals(50, engine.mobilityPercent(tiles))
    }

    @Test
    fun `mobility percent is 100 when every house touches a road`() {
        val tiles = listOf(road(0, 0), house(0, 1), road(1, 0), house(2, 0))
        assertEquals(100, engine.mobilityPercent(tiles))
    }

    @Test
    fun `coverage is empty when there are no services`() {
        val tiles = listOf(road(0, 0), house(0, 1))
        val coverage = engine.computeCoverage(tiles, InfraCategory.EDUCATION)
        assertTrue(coverage.coveredHouses.isEmpty())
        assertEquals(1, coverage.totalHouses)
    }

    @Test
    fun `coverage is empty when there are no houses`() {
        val tiles = listOf(road(0, 0), school(0, 1))
        val coverage = engine.computeCoverage(tiles, InfraCategory.EDUCATION)
        assertEquals(0, coverage.totalHouses)
        assertTrue(coverage.coveredHouses.isEmpty())
    }

    @Test
    fun `house within radius through connected road is covered`() {
        val tiles = listOf(
            road(0, 0), road(0, 1), road(0, 2),
            house(1, 0), school(1, 2, radius = 6)
        )
        val coverage = engine.computeCoverage(tiles, InfraCategory.EDUCATION)
        assertTrue(GridPosition(1, 0) in coverage.coveredHouses)
        assertEquals(100, coverage.coveragePercent)
    }

    @Test
    fun `house beyond coverage radius is not covered`() {
        val tiles = listOf(
            road(0, 0), road(0, 1), road(0, 2), road(0, 3), road(0, 4),
            house(1, 0), school(1, 4, radius = 1)
        )
        val coverage = engine.computeCoverage(tiles, InfraCategory.EDUCATION)
        assertFalse(GridPosition(1, 0) in coverage.coveredHouses)
    }

    @Test
    fun `house and service on different road components are not connected`() {
        val tiles = listOf(
            road(0, 0), house(0, 1),
            road(5, 5), school(5, 6, radius = 6)
        )
        val coverage = engine.computeCoverage(tiles, InfraCategory.EDUCATION)
        assertTrue(coverage.coveredHouses.isEmpty())
    }

    @Test
    fun `a house directly adjacent to a service without roads between is still evaluated via road entry`() {
        // La casa y el colegio comparten la misma calle de entrada.
        val tiles = listOf(road(2, 2), house(2, 1), school(2, 3, radius = 2))
        val coverage = engine.computeCoverage(tiles, InfraCategory.EDUCATION)
        assertTrue(GridPosition(2, 1) in coverage.coveredHouses)
    }

    @Test
    fun `coverage percent rounds down using integer division`() {
        val tiles = listOf(
            road(0, 0), road(0, 1),
            house(1, 0), house(1, 1), house(1, 2),
            school(0, 1, radius = 6)
        )
        // Sólo las casas conectadas a la carretera (fila 1, col 0 y 1) están cubiertas; col 2 no toca carretera.
        val coverage = engine.computeCoverage(tiles, InfraCategory.EDUCATION)
        assertEquals(3, coverage.totalHouses)
        assertEquals(2, coverage.coveredHouses.size)
        assertEquals(66, coverage.coveragePercent)
    }
}
