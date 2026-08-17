package com.educalab.atumanera.domain.logic

/** Resultado de intentar una operación de presupuesto. */
sealed class BudgetResult {
    data class Approved(val newSpent: Int) : BudgetResult()
    data class Rejected(val reason: String) : BudgetResult()
}

/**
 * Reglas de negocio del presupuesto ficticio de la ciudad.
 * No permite gastar más del total asignado ni quedar en negativo.
 */
class BudgetManager {

    fun canAfford(currentSpent: Int, totalBudget: Int, cost: Int): Boolean {
        if (cost <= 0) return false
        return currentSpent + cost <= totalBudget
    }

    fun place(currentSpent: Int, totalBudget: Int, cost: Int): BudgetResult {
        if (cost <= 0) return BudgetResult.Rejected("El coste debe ser positivo")
        val newSpent = currentSpent + cost
        return if (newSpent > totalBudget) {
            BudgetResult.Rejected("Presupuesto insuficiente")
        } else {
            BudgetResult.Approved(newSpent)
        }
    }

    fun refund(currentSpent: Int, cost: Int): BudgetResult {
        if (cost <= 0) return BudgetResult.Rejected("El coste debe ser positivo")
        val newSpent = (currentSpent - cost).coerceAtLeast(0)
        return BudgetResult.Approved(newSpent)
    }
}
