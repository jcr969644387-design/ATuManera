package com.educalab.atumanera.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.educalab.atumanera.data.local.entity.CityEntity
import com.educalab.atumanera.data.local.entity.CityMetricEntity
import com.educalab.atumanera.data.local.entity.CityTileEntity
import com.educalab.atumanera.data.local.entity.InfrastructureTypeEntity
import com.educalab.atumanera.data.local.entity.PlacedInfrastructureEntity
import com.educalab.atumanera.data.local.entity.UserProfileEntity
import com.educalab.atumanera.data.repository.CityRepository
import com.educalab.atumanera.data.repository.PlacementOutcome
import com.educalab.atumanera.data.seed.DEFAULT_CITY_COLS
import com.educalab.atumanera.data.seed.DEFAULT_CITY_ROWS
import com.educalab.atumanera.domain.model.InfraCategory
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Una casilla ya resuelta para pintar en pantalla (posición + infraestructura si existe). */
data class TileVisual(
    val tile: CityTileEntity,
    val placement: PlacedInfrastructureEntity?,
    val infraType: InfrastructureTypeEntity?
)

data class CityBoardState(
    val user: UserProfileEntity? = null,
    val city: CityEntity? = null,
    val tiles: List<TileVisual> = emptyList(),
    val catalog: List<InfrastructureTypeEntity> = emptyList(),
    val latestMetric: CityMetricEntity? = null,
    val metricHistory: List<CityMetricEntity> = emptyList(),
    val isReady: Boolean = false
)

sealed class CityEvent {
    object Placed : CityEvent()
    object Removed : CityEvent()
    data class MissionsCompleted(val codes: List<String>) : CityEvent()
    data class BadgesEarned(val codes: List<String>) : CityEvent()
    data class LevelCompleted(val level: Int) : CityEvent()
    data class Rejected(val reason: String) : CityEvent()
}

/**
 * @param freeMode si es true, este ViewModel opera sobre la ciudad de Modo
 * Libre (presupuesto prácticamente ilimitado, sin misiones ni insignias) en
 * lugar de la ciudad principal ligada a las misiones.
 */
class CityViewModel(
    private val repository: CityRepository,
    private val freeMode: Boolean = false
) : ViewModel() {

    private val user = repository.observeUserFlow()
    private val cityFlow = user.flatMapLatest { u ->
        if (u == null) kotlinx.coroutines.flow.flowOf(null)
        else if (freeMode) repository.observeFreeCityFlow(u.id) else repository.observeCityFlow(u.id)
    }

    init {
        if (freeMode) {
            viewModelScope.launch {
                val u = user.filterNotNull().first()
                repository.ensureFreeCity(u.id, DEFAULT_CITY_ROWS, DEFAULT_CITY_COLS)
            }
        }
    }

    private val tilesAndPlacements = cityFlow.filterNotNull().flatMapLatest { city ->
        combine(
            repository.observeTiles(city.id),
            repository.observePlacements(city.id),
            repository.observeInfrastructureCatalog()
        ) { tiles, placements, catalog ->
            val placementByTile = placements.associateBy { it.tileId }
            val catalogById = catalog.associateBy { it.id }
            tiles.map { tile ->
                val placement = placementByTile[tile.id]
                TileVisual(tile, placement, placement?.let { catalogById[it.infrastructureTypeId] })
            } to catalog
        }
    }

    private val metricsFlow = cityFlow.filterNotNull().flatMapLatest { city ->
        repository.observeMetricsHistory(city.id)
    }

    val state: StateFlow<CityBoardState> = combine(user, cityFlow, tilesAndPlacements, metricsFlow) { u, city, tilesCatalog, history ->
        CityBoardState(
            user = u,
            city = city,
            tiles = tilesCatalog.first,
            catalog = tilesCatalog.second,
            latestMetric = history.lastOrNull(),
            metricHistory = history,
            isReady = u != null && city != null
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CityBoardState())

    private val _events = MutableSharedFlow<CityEvent>(extraBufferCapacity = 4)
    val events: SharedFlow<CityEvent> = _events

    fun place(row: Int, col: Int, infrastructureTypeId: Long) {
        val u = state.value.user ?: return
        val city = state.value.city ?: return
        viewModelScope.launch {
            val outcome = if (freeMode) {
                repository.placeInfrastructureFree(city.id, row, col, infrastructureTypeId)
            } else {
                repository.placeInfrastructure(u.id, city.id, row, col, infrastructureTypeId)
            }
            when (outcome) {
                is PlacementOutcome.Success -> {
                    _events.tryEmit(CityEvent.Placed)
                    if (outcome.newlyCompletedMissions.isNotEmpty()) _events.tryEmit(CityEvent.MissionsCompleted(outcome.newlyCompletedMissions))
                    if (outcome.newBadges.isNotEmpty()) _events.tryEmit(CityEvent.BadgesEarned(outcome.newBadges))
                    outcome.newlyCompletedLevels.forEach { level -> _events.tryEmit(CityEvent.LevelCompleted(level)) }
                }
                PlacementOutcome.InsufficientBudget -> _events.tryEmit(CityEvent.Rejected("No tienes presupuesto suficiente para esta construcción."))
                PlacementOutcome.TileOccupied -> _events.tryEmit(CityEvent.Rejected("Esa casilla ya tiene una construcción."))
                PlacementOutcome.TileNotFound -> _events.tryEmit(CityEvent.Rejected("Esa casilla no está disponible."))
            }
        }
    }

    fun remove(row: Int, col: Int) {
        val u = state.value.user ?: return
        val city = state.value.city ?: return
        viewModelScope.launch {
            if (freeMode) repository.removeInfrastructureFree(city.id, row, col)
            else repository.removeInfrastructure(u.id, city.id, row, col)
            _events.tryEmit(CityEvent.Removed)
        }
    }

    fun clearCity() {
        val u = state.value.user ?: return
        val city = state.value.city ?: return
        viewModelScope.launch {
            if (freeMode) repository.clearCityFree(city.id) else repository.clearCity(u.id, city.id)
            _events.tryEmit(CityEvent.Removed)
        }
    }

    fun clearCategory(category: InfraCategory) {
        val u = state.value.user ?: return
        val city = state.value.city ?: return
        viewModelScope.launch {
            if (freeMode) repository.clearCategoryFree(city.id, category) else repository.clearCategory(u.id, city.id, category)
            _events.tryEmit(CityEvent.Removed)
        }
    }

    fun updateProfile(alias: String, avatarCode: String) {
        val u = state.value.user ?: return
        viewModelScope.launch {
            repository.updateProfile(u.id, alias.ifBlank { "Alcalde" }, avatarCode)
        }
    }
}

class CityViewModelFactory(
    private val repository: CityRepository,
    private val freeMode: Boolean = false
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CityViewModel::class.java)) {
            return CityViewModel(repository, freeMode) as T
        }
        throw IllegalArgumentException("ViewModel desconocido: $modelClass")
    }
}
