package com.educalab.atumanera.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.educalab.atumanera.data.local.entity.MissionEntity
import com.educalab.atumanera.data.repository.CityRepository
import com.educalab.atumanera.domain.model.MissionStatus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

data class MissionUiItem(
    val mission: MissionEntity,
    val status: MissionStatus,
    val progressPercent: Int
)

data class MissionsUiState(
    val items: List<MissionUiItem> = emptyList(),
    val totalXp: Int = 0,
    val missionsCompleted: Int = 0
)

class MissionsViewModel(private val repository: CityRepository) : ViewModel() {

    private val user = repository.observeUserFlow()
    private val cityFlow = user.flatMapLatest { u ->
        if (u == null) flowOf(null) else repository.observeCityFlow(u.id)
    }

    val state: StateFlow<MissionsUiState> = combine(user, cityFlow, repository.observeMissions()) { u, city, missions ->
        Triple(u, city, missions)
    }.flatMapLatest { (u, city, missions) ->
        if (u == null || city == null) {
            flowOf(MissionsUiState())
        } else {
            combine(repository.observeMissionProgress(u.id, city.id), repository.observeProgress(u.id)) { progressList, progress ->
                val progressByMission = progressList.associateBy { it.missionId }
                val items = missions.map { mission ->
                    val p = progressByMission[mission.id]
                    val status = when (p?.status) {
                        "COMPLETED" -> MissionStatus.COMPLETED
                        "IN_PROGRESS" -> MissionStatus.IN_PROGRESS
                        "AVAILABLE" -> MissionStatus.AVAILABLE
                        "LOCKED" -> MissionStatus.LOCKED
                        else -> if (mission.orderIndex == 1) MissionStatus.AVAILABLE else MissionStatus.LOCKED
                    }
                    MissionUiItem(mission, status, p?.progressPercent ?: 0)
                }
                MissionsUiState(items, progress?.totalXp ?: 0, progress?.missionsCompleted ?: 0)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MissionsUiState())
}

class MissionsViewModelFactory(private val repository: CityRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MissionsViewModel::class.java)) return MissionsViewModel(repository) as T
        throw IllegalArgumentException("ViewModel desconocido: $modelClass")
    }
}
