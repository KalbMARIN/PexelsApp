@file:OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)

package com.practicum.pexelsapp.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.practicum.pexelsapp.data.mapper.toUiModel
import com.practicum.pexelsapp.domain.usecase.GetCuratedPhotosUseCase
import com.practicum.pexelsapp.domain.usecase.GetFeaturedCollectionsUseCase
import com.practicum.pexelsapp.domain.usecase.SearchPhotosUseCase
import com.practicum.pexelsapp.presentation.model.PhotoUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCuratedPhotosUseCase: GetCuratedPhotosUseCase,
    private val getFeaturedCollectionsUseCase: GetFeaturedCollectionsUseCase,
    private val searchPhotosUseCase: SearchPhotosUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private val _internalCategories = MutableStateFlow<List<String>>(emptyList())



    val photos: Flow<PagingData<PhotoUiModel>> = _state
        .map { it.searchQuery }
        .debounce(500)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isEmpty()) {
                getCuratedPhotosUseCase()
            } else {
                searchPhotosUseCase(query)
            }
        }
        .map { pagingData ->
            pagingData.map { it.toUiModel() }
        }
        .onEach {
            if (!_state.value.isReady) _state.update {
                it.copy(isReady = true)
            }
        }
        .cachedIn(viewModelScope)

    val displayCategories: StateFlow<List<String>> = combine(
        _internalCategories,
        _state.map { it.searchQuery }.distinctUntilChanged()
    ) { categories, query ->
        if (query.isEmpty()) categories
        else {
            val active = categories.find { it.equals(query, ignoreCase = true) }
            if (active != null) listOf(active) + (categories - active) else categories
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        loadCategories()
    }

    fun processCommand(command: HomeCommand) {
        when (command) {
            is HomeCommand.SearchQueryChanged -> {
                _state.update { it.copy(searchQuery = command.query) }
            }
            is HomeCommand.CategoryClicked -> {
                _state.update { it.copy(searchQuery = command.category) }
            }
            HomeCommand.Retry -> {
                loadCategories()
                _state.update { it.copy(searchQuery = _state.value.searchQuery) }
            }
        }
    }


    private fun loadCategories() {
        viewModelScope.launch {
            getFeaturedCollectionsUseCase().onSuccess { list ->
                if (list.isNotEmpty() && _internalCategories.value.isEmpty()) {
                    _internalCategories.value = list
                }
            }
        }
    }


}

sealed interface HomeCommand {
    data class SearchQueryChanged(val query: String) : HomeCommand
    data class CategoryClicked(val category: String) : HomeCommand
    object Retry : HomeCommand
}

data class HomeState(
    val searchQuery: String = "",
    val isReady: Boolean = false
)

sealed class HomeUiState {
    object Loading : HomeUiState()
    object Success : HomeUiState()
    object Empty : HomeUiState()
    object Error : HomeUiState()
}
