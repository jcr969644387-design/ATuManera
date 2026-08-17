package com.educalab.atumanera.domain.logic

import com.educalab.atumanera.domain.model.CityMetricsSnapshot
import com.educalab.atumanera.domain.model.RequirementType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MissionEvaluatorTest {

    private val evaluator = MissionEvaluator()

    private fun metrics(mobility: Int = 0, services: Int = 0, green: Int = 0) = CityMetricsSnapshot(
        mobility = mobility, servicesScore = services, greenScore = green,
        educationCoverage = 0, healthCoverage = 0, waterCoverage = 0,
        budgetSpent = 0, budgetTotal = 1000
    )

    @Test
    fun `mission with no requirements is never complete`() {
        val result = evaluator.evaluate(1, emptyList(), CityStateSnapshot(emptyMap(), metrics(), 0, emptyMap()))
        assertFalse(result.isComplete)
        assertEquals(0, result.progressPercent)
    }

    @Test
    fun `place count requirement completes when target is reached`() {
        val requirements = listOf(MissionRequirementInput(RequirementType.PLACE_COUNT, "ROAD_BASIC", 3))
        val state = CityStateSnapshot(mapOf("ROAD_BASIC" to 3), metrics(), 3, emptyMap())
        val result = evaluator.evaluate(1, requirements, state)
        assertTrue(result.isComplete)
        assertEquals(100, result.progressPercent)
    }

    @Test
    fun `place count requirement reports partial progress`() {
        val requirements = listOf(MissionRequirementInput(RequirementType.PLACE_COUNT, "ROAD_BASIC", 4))
        val state = CityStateSnapshot(mapOf("ROAD_BASIC" to 2), metrics(), 2, emptyMap())
        val result = evaluator.evaluate(1, requirements, state)
        assertFalse(result.isComplete)
        assertEquals(50, result.progressPercent)
    }

    @Test
    fun `metric threshold requirement uses the correct metric key`() {
        val requirements = listOf(MissionRequirementInput(RequirementType.METRIC_THRESHOLD, "MOBILITY", 80))
        val state = CityStateSnapshot(emptyMap(), metrics(mobility = 80), 0, emptyMap())
        val result = evaluator.evaluate(1, requirements, state)
        assertTrue(result.isComplete)
    }

    @Test
    fun `road network size requirement checks the largest connected component`() {
        val requirements = listOf(MissionRequirementInput(RequirementType.ROAD_NETWORK_SIZE, "ANY", 10))
        val incomplete = evaluator.evaluate(1, requirements, CityStateSnapshot(emptyMap(), metrics(), 5, emptyMap()))
        val complete = evaluator.evaluate(1, requirements, CityStateSnapshot(emptyMap(), metrics(), 10, emptyMap()))
        assertFalse(incomplete.isComplete)
        assertTrue(complete.isComplete)
    }

    @Test
    fun `coverage count requirement reads from the coverage map`() {
        val requirements = listOf(MissionRequirementInput(RequirementType.COVERAGE_COUNT, "EDUCATION", 3))
        val state = CityStateSnapshot(emptyMap(), metrics(), 0, mapOf("EDUCATION" to 3))
        val result = evaluator.evaluate(1, requirements, state)
        assertTrue(result.isComplete)
    }

    @Test
    fun `budget efficiency requirement rewards low spending`() {
        val requirements = listOf(MissionRequirementInput(RequirementType.BUDGET_EFFICIENCY, "ANY", 40))
        val efficientMetrics = metrics().copy(budgetSpent = 500, budgetTotal = 1000) // 50% usado -> 50% eficiencia
        val state = CityStateSnapshot(emptyMap(), efficientMetrics, 0, emptyMap())
        val result = evaluator.evaluate(1, requirements, state)
        assertTrue(result.isComplete)
    }

    @Test
    fun `mission with multiple requirements needs all of them satisfied`() {
        val requirements = listOf(
            MissionRequirementInput(RequirementType.METRIC_THRESHOLD, "MOBILITY", 80),
            MissionRequirementInput(RequirementType.METRIC_THRESHOLD, "SERVICES", 80)
        )
        val partial = CityStateSnapshot(emptyMap(), metrics(mobility = 80, services = 40), 0, emptyMap())
        val result = evaluator.evaluate(1, requirements, partial)
        assertFalse(result.isComplete)
        assertEquals(75, result.progressPercent)
    }
}
