@file:OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)

package com.practicum.pexelsapp.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.practicum.pexelsapp.domain.entity.Photo
import com.practicum.pexelsapp.domain.usecase.GetCuratedPhotosUseCase
import com.practicum.pexelsapp.domain.usecase.GetFeaturedCollectionsUseCase
import com.practicum.pexelsapp.domain.usecase.SearchPhotosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCuratedPhotosUseCase: GetCuratedPhotosUseCase,
    private val getFeaturedCollectionsUseCase: GetFeaturedCollectionsUseCase,
    private val searchPhotosUseCase: SearchPhotosUseCase
) : ViewModel() {

    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQueryText = _searchQuery.asStateFlow()


    val photos: Flow<PagingData<Photo>> = _searchQuery
        .debounce(500)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isEmpty()) {
                getCuratedPhotosUseCase()
            } else {
                searchPhotosUseCase(query)
            }
        }
        .onEach {
            _isReady.value = true
        }
        .cachedIn(viewModelScope)

    val displayCategories: StateFlow<List<String>> = combine(_categories, _searchQuery) { categories, query ->
        if (query.isEmpty()) {
            categories
        } else {
            val activeCategory = categories.find { it.equals(query, ignoreCase = true) }
            if (activeCategory != null) {
                listOf(activeCategory) + (categories - activeCategory)
            } else {
                categories
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        viewModelScope.launch {
            photos.collect { }
        }

        viewModelScope.launch {
            photos.collectLatest {
                if (_categories.value.isEmpty()) {
                    loadCategories()
                }
            }
        }
        loadCategories()
    }


    fun loadCategories() {
        viewModelScope.launch {
            getFeaturedCollectionsUseCase().onSuccess { list ->
                if (list.isNotEmpty()) {
                    _categories.value = list
                }
            }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun retryAll() {
        loadCategories()
        _searchQuery.value = _searchQuery.value
    }

}

sealed class HomeUiState {
    object Loading : HomeUiState()
    object Success : HomeUiState()
    object Empty : HomeUiState()
    object Error : HomeUiState()
}