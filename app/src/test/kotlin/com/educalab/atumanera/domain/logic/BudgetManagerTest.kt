package com.educalab.atumanera.domain.logic

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BudgetManagerTest {

    private val manager = BudgetManager()

    @Test
    fun `can afford when cost fits exactly in remaining budget`() {
        assertTrue(manager.canAfford(currentSpent = 900, totalBudget = 1000, cost = 100))
    }

    @Test
    fun `cannot afford when cost exceeds remaining budget by one`() {
        assertTrue(!manager.canAfford(currentSpent = 900, totalBudget = 1000, cost = 101))
    }

    @Test
    fun `cannot afford a non positive cost`() {
        assertTrue(!manager.canAfford(currentSpent = 0, totalBudget = 1000, cost = 0))
        assertTrue(!manager.canAfford(currentSpent = 0, totalBudget = 1000, cost = -10))
    }

    @Test
    fun `place approves and returns the new spent amount`() {
        val result = manager.place(currentSpent = 100, totalBudget = 1000, cost = 50)
        assertTrue(result is BudgetResult.Approved)
        assertEquals(150, (result as BudgetResult.Approved).newSpent)
    }

    @Test
    fun `place rejects when budget is insufficient`() {
        val result = manager.place(currentSpent = 980, totalBudget = 1000, cost = 30)
        assertTrue(result is BudgetResult.Rejected)
    }

    @Test
    fun `place rejects a zero or negative cost`() {
        val result = manager.place(currentSpent = 0, totalBudget = 1000, cost = 0)
        assertTrue(result is BudgetResult.Rejected)
    }

    @Test
    fun `refund reduces the spent amount`() {
        val result = manager.refund(currentSpent = 200, cost = 50)
        assertTrue(result is BudgetResult.Approved)
        assertEquals(150, (result as BudgetResult.Approved).newSpent)
    }

    @Test
    fun `refund never leaves spent below zero`() {
        val result = manager.refund(currentSpent = 20, cost = 50)
        assertTrue(result is BudgetResult.Approved)
        assertEquals(0, (result as BudgetResult.Approved).newSpent)
    }

    @Test
    fun `refund rejects a non positive cost`() {
        val result = manager.refund(currentSpent = 100, cost = 0)
        assertTrue(result is BudgetResult.Rejected)
    }
}
