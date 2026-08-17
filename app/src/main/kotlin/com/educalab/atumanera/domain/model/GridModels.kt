package com.educalab.atumanera.domain.model

/** Posición de una casilla dentro de la cuadrícula de la ciudad. */
data class GridPosition(val row: Int, val col: Int) {
    fun neighbors(rows: Int, cols: Int): List<GridPosition> = buildList {
        if (row > 0) add(GridPosition(row - 1, col))
        if (row < rows - 1) add(GridPosition(row + 1, col))
        if (col > 0) add(GridPosition(row, col - 1))
        if (col < cols - 1) add(GridPosition(row, col + 1))
    }
}

/**
 * Instantánea de una casilla con la infraestructura (si la hay) colocada en ella.
 * Es un modelo puro, sin dependencias de Room, usado por el motor de reglas.
 */
data class TileSnapshot(
    val position: GridPosition,
    val infraCode: String?,
    val category: InfraCategory?,
    val coverageRadius: Int = 0
)

/** Resultado de analizar la red de carreteras de una ciudad. */
data class RoadNetworkResult(
    /** Mapa de posición -> id de componente conectado (mismo id = mismo tramo de carretera). */
    val componentOf: Map<GridPosition, Int>,
    /** Tamaño de cada componente conectado. */
    val componentSizes: Map<Int, Int>
) {
    fun componentSizeAt(position: GridPosition): Int {
        val id = componentOf[position] ?: return 0
        return componentSizes[id] ?: 0
    }

    fun largestComponentSize(): Int = componentSizes.values.maxOrNull() ?: 0
}

/** Resultado de calcular la cobertura de un tipo de servicio sobre las viviendas. */
data class CoverageResult(
    val category: InfraCategory,
    /** Conjunto de casillas de vivienda que quedan cubiertas por el servicio. */
    val coveredHouses: Set<GridPosition>,
    val totalHouses: Int
) {
    val coveragePercent: Int
        get() = if (totalHouses == 0) 0 else ((coveredHouses.size * 100) / totalHouses)
}

/** Conjunto de indicadores calculados para una ciudad en un instante dado. */
data class CityMetricsSnapshot(
    val mobility: Int,
    val servicesScore: Int,
    val greenScore: Int,
    val educationCoverage: Int,
    val healthCoverage: Int,
    val waterCoverage: Int,
    val budgetSpent: Int,
    val budgetTotal: Int
) {
    val budgetRemaining: Int get() = (budgetTotal - budgetSpent).coerceAtLeast(0)
    val budgetUsedPercent: Int get() = if (budgetTotal == 0) 0 else ((budgetSpent * 100) / budgetTotal)
}

/** Estado agregado de una misión frente al progreso real de la ciudad. */
data class MissionEvaluation(
    val missionId: Long,
    val isComplete: Boolean,
    val progressPercent: Int
)
