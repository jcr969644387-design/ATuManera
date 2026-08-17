package com.educalab.atumanera.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.educalab.atumanera.data.local.entity.BadgeEntity
import com.educalab.atumanera.data.local.entity.DecorationEntity
import com.educalab.atumanera.data.repository.CityRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

data class BadgeUiItem(val badge: BadgeEntity, val earned: Boolean)
data class DecorationUiItem(val decoration: DecorationEntity, val unlocked: Boolean)

data class CollectionUiState(
    val badges: List<BadgeUiItem> = emptyList(),
    val decorations: List<DecorationUiItem> = emptyList()
)

class CollectionViewModel(private val repository: CityRepository) : ViewModel() {

    private val user = repository.observeUserFlow()

    val state: StateFlow<CollectionUiState> = user.flatMapLatest { u ->
        if (u == null) {
            flowOf(CollectionUiState())
        } else {
            combine(
                repository.observeBadges(),
                repository.observeUserBadges(u.id),
                repository.observeDecorations(),
                repository.observeUnlockedDecorations(u.id)
            ) { badges, userBadges, decorations, unlockedDecos ->
                val earnedIds = userBadges.map { it.badgeId }.toSet()
                val unlockedIds = unlockedDecos.map { it.decorationId }.toSet()
                CollectionUiState(
                    badges = badges.map { BadgeUiItem(it, it.id in earnedIds) },
                    decorations = decorations.map { DecorationUiItem(it, it.id in unlockedIds) }
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CollectionUiState())
}

class CollectionViewModelFactory(private val repository: CityRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CollectionViewModel::class.java)) return CollectionViewModel(repository) as T
        throw IllegalArgumentException("ViewModel desconocido: $modelClass")
    }
}
