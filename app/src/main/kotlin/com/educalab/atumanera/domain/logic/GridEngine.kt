package com.educalab.atumanera.domain.logic

import com.educalab.atumanera.domain.model.CoverageResult
import com.educalab.atumanera.domain.model.GridPosition
import com.educalab.atumanera.domain.model.InfraCategory
import com.educalab.atumanera.domain.model.RoadNetworkResult
import com.educalab.atumanera.domain.model.TileSnapshot
import java.util.ArrayDeque

/**
 * Motor de reglas de la ciudad: calcula conectividad de carreteras y cobertura
 * de servicios (educación, salud, agua, parques) mediante BFS real sobre la
 * cuadrícula. No depende de Android ni de Room: es 100% testeable en JVM.
 */
class GridEngine(private val rows: Int, private val cols: Int) {

    /**
     * Calcula las componentes conexas de la red de carreteras usando BFS.
     * Dos casillas de carretera están en la misma componente si existe un
     * camino de casillas de carretera adyacentes (4 direcciones) entre ellas.
     */
    fun computeRoadNetwork(tiles: List<TileSnapshot>): RoadNetworkResult {
        val roadPositions = tiles.filter { it.category == InfraCategory.ROAD }
            .map { it.position }
            .toSet()

        val componentOf = mutableMapOf<GridPosition, Int>()
        val componentSizes = mutableMapOf<Int, Int>()
        var nextComponentId = 0

        for (start in roadPositions) {
            if (componentOf.containsKey(start)) continue
            val componentId = nextComponentId++
            var size = 0
            val queue = ArrayDeque<GridPosition>()
            queue.add(start)
            componentOf[start] = componentId
            while (queue.isNotEmpty()) {
                val current = queue.poll()
                size++
                for (neighbor in current.neighbors(rows, cols)) {
                    if (neighbor in roadPositions && !componentOf.containsKey(neighbor)) {
                        componentOf[neighbor] = componentId
                        queue.add(neighbor)
                    }
                }
            }
            componentSizes[componentId] = size
        }

        return RoadNetworkResult(componentOf, componentSizes)
    }

    /**
     * Determina si una casilla (vivienda o servicio) tiene acceso directo a la
     * red de carreteras: ella misma es carretera o es adyacente a una carretera.
     */
    fun roadComponentOfAdjacentTile(
        position: GridPosition,
        roadNetwork: RoadNetworkResult,
        occupiedByRoad: Set<GridPosition>
    ): Int? {
        if (position in occupiedByRoad) return roadNetwork.componentOf[position]
        for (neighbor in position.neighbors(rows, cols)) {
            if (neighbor in occupiedByRoad) {
                roadNetwork.componentOf[neighbor]?.let { return it }
            }
        }
        return null
    }

    /**
     * Calcula qué casillas de vivienda quedan cubiertas por un tipo de servicio.
     *
     * Reglas reales:
     * 1. La vivienda debe tener acceso a la red de carreteras.
     * 2. Debe existir al menos un edificio del servicio con acceso a esa MISMA
     *    componente de carretera.
     * 3. La distancia en saltos de carretera (BFS) entre la vivienda y el
     *    servicio más cercano debe ser <= al radio de cobertura del servicio.
     */
    fun computeCoverage(
        tiles: List<TileSnapshot>,
        serviceCategory: InfraCategory
    ): CoverageResult {
        val roadTiles = tiles.filter { it.category == InfraCategory.ROAD }.map { it.position }.toSet()
        val roadNetwork = computeRoadNetwork(tiles)

        val houses = tiles.filter { it.category == InfraCategory.HOUSING }
        val services = tiles.filter { it.category == serviceCategory }

        if (services.isEmpty() || houses.isEmpty()) {
            return CoverageResult(serviceCategory, emptySet(), houses.size)
        }

        // Punto de entrada a la red de carreteras para cada servicio, junto a su radio.
        data class ServiceEntry(val roadEntry: GridPosition, val component: Int, val radius: Int)

        val serviceEntries = services.mapNotNull { service ->
            val entry = findRoadEntry(service.position, roadTiles) ?: return@mapNotNull null
            val component = roadNetwork.componentOf[entry] ?: return@mapNotNull null
            ServiceEntry(entry, component, service.coverageRadius)
        }

        if (serviceEntries.isEmpty()) {
            return CoverageResult(serviceCategory, emptySet(), houses.size)
        }

        val covered = mutableSetOf<GridPosition>()
        for (house in houses) {
            val houseEntry = findRoadEntry(house.position, roadTiles) ?: continue
            val houseComponent = roadNetwork.componentOf[houseEntry] ?: continue

            val candidateEntries = serviceEntries.filter { it.component == houseComponent }
            if (candidateEntries.isEmpty()) continue

            val minDistance = candidateEntries.minOf { serviceEntry ->
                bfsDistance(houseEntry, serviceEntry.roadEntry, roadTiles)
                    ?.let { dist -> dist to serviceEntry.radius }
                    ?.let { (dist, radius) -> if (dist <= radius) dist else Int.MAX_VALUE }
                    ?: Int.MAX_VALUE
            }
            if (minDistance != Int.MAX_VALUE) {
                covered.add(house.position)
            }
        }

        return CoverageResult(serviceCategory, covered, houses.size)
    }

    /** Encuentra la casilla de carretera de entrada más cercana a una posición (ella misma o vecina). */
    private fun findRoadEntry(position: GridPosition, roadTiles: Set<GridPosition>): GridPosition? {
        if (position in roadTiles) return position
        return position.neighbors(rows, cols).firstOrNull { it in roadTiles }
    }

    /** Distancia BFS (en número de casillas de carretera) entre dos puntos de la red. */
    private fun bfsDistance(from: GridPosition, to: GridPosition, roadTiles: Set<GridPosition>): Int? {
        if (from == to) return 0
        val visited = mutableSetOf(from)
        val queue = ArrayDeque<Pair<GridPosition, Int>>()
        queue.add(from to 0)
        while (queue.isNotEmpty()) {
            val (current, dist) = queue.poll()
            for (neighbor in current.neighbors(rows, cols)) {
                if (neighbor !in roadTiles || neighbor in visited) continue
                if (neighbor == to) return dist + 1
                visited.add(neighbor)
                queue.add(neighbor to dist + 1)
            }
        }
        return null
    }

    /** Porcentaje de viviendas con acceso directo (ella o vecina) a la red de carreteras. */
    fun mobilityPercent(tiles: List<TileSnapshot>): Int {
        val roadTiles = tiles.filter { it.category == InfraCategory.ROAD }.map { it.position }.toSet()
        val houses = tiles.filter { it.category == InfraCategory.HOUSING }
        if (houses.isEmpty()) return 0
        val connected = houses.count { findRoadEntry(it.position, roadTiles) != null }
        return (connected * 100) / houses.size
    }
}
