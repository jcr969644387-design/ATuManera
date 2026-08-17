package com.educalab.atumanera.domain.logic

import com.educalab.atumanera.domain.model.CityMetricsSnapshot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UnlockEvaluatorTest {

    private val evaluator = UnlockEvaluator()

    private fun metrics(mobility: Int = 0, green: Int = 0, health: Int = 0, water: Int = 0, education: Int = 0, services: Int = 0) =
        CityMetricsSnapshot(mobility, services, green, education, health, water, 0, 1000)

    @Test
    fun `no badges unlock with an empty context`() {
        val context = UnlockContext(totalXp = 0, missionsCompleted = 0, metrics = metrics(), placedCounts = emptyMap())
        val unlocked = evaluator.evaluateBadges(context, UnlockEvaluator.defaultBadgeConditions())
        assertTrue(unlocked.isEmpty())
    }

    @Test
    fun `first road badge unlocks after placing one road`() {
        val context = UnlockContext(0, 0, metrics(), mapOf("ROAD_BASIC" to 1))
        val unlocked = evaluator.evaluateBadges(context, UnlockEvaluator.defaultBadgeConditions())
        assertTrue("BADGE_FIRST_ROAD" in unlocked)
    }

    @Test
    fun `mobility master badge requires 90 percent mobility`() {
        val below = UnlockContext(0, 0, metrics(mobility = 89), emptyMap())
        val above = UnlockContext(0, 0, metrics(mobility = 90), emptyMap())
        assertFalse("BADGE_MOBILITY_MASTER" in evaluator.evaluateBadges(below, UnlockEvaluator.defaultBadgeConditions()))
        assertTrue("BADGE_MOBILITY_MASTER" in evaluator.evaluateBadges(above, UnlockEvaluator.defaultBadgeConditions()))
    }

    @Test
    fun `xp badge unlocks exactly at threshold`() {
        val context = UnlockContext(totalXp = 500, missionsCompleted = 0, metrics = metrics(), placedCounts = emptyMap())
        assertTrue("BADGE_XP_500" in evaluator.evaluateBadges(context, UnlockEvaluator.defaultBadgeConditions()))
    }

    @Test
    fun `mission count badges unlock progressively`() {
        val ten = UnlockContext(0, 10, metrics(), emptyMap())
        val unlockedAtTen = evaluator.evaluateBadges(ten, UnlockEvaluator.defaultBadgeConditions())
        assertTrue("BADGE_MISSION_10" in unlockedAtTen)
        assertFalse("BADGE_MISSION_20" in unlockedAtTen)
    }

    @Test
    fun `decorations unlock based on their own thresholds`() {
        val context = UnlockContext(totalXp = 300, missionsCompleted = 5, metrics = metrics(green = 75, mobility = 90), placedCounts = emptyMap())
        val decorations = evaluator.evaluateDecorations(context, UnlockEvaluator.defaultDecorationConditions())
        assertTrue("DECO_FOUNTAIN" in decorations)
        assertTrue("DECO_STATUE" in decorations)
        assertTrue("DECO_GARDEN" in decorations)
        assertTrue("DECO_BRIDGE" in decorations)
        assertTrue("DECO_OBELISK" in decorations)
    }

    @Test
    fun `decorations requiring higher metrics stay locked when not met`() {
        val context = UnlockContext(totalXp = 0, missionsCompleted = 0, metrics = metrics(), placedCounts = emptyMap())
        val decorations = evaluator.evaluateDecorations(context, UnlockEvaluator.defaultDecorationConditions())
        assertTrue(decorations.isEmpty())
    }
}
