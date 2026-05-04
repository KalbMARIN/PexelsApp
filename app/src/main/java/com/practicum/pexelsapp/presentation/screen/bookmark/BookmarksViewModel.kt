package com.practicum.pexelsapp.presentation.screen.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.practicum.pexelsapp.data.mapper.toUiModel
import com.practicum.pexelsapp.domain.usecase.GetBookmarksUseCase
import com.practicum.pexelsapp.presentation.model.PhotoUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val getBookmarksUseCase: GetBookmarksUseCase
) : ViewModel() {

    val bookmarks: Flow<PagingData<PhotoUiModel>> = getBookmarksUseCase()
        .map { pagingData ->
            pagingData.map { it.toUiModel() }
        }
        .cachedIn(viewModelScope)

    fun processCommand(command: BookmarksCommand) {
        when (command) {
            is BookmarksCommand.PhotoClicked -> {

            }
            BookmarksCommand.ExploreClicked -> {

            }
        }
    }
}


sealed interface BookmarksCommand {
    data class PhotoClicked(val id: Int) : BookmarksCommand
    object ExploreClicked : BookmarksCommand
}