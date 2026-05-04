package com.practicum.pexelsapp.presentation.screen.details

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.pexelsapp.data.mapper.toDomain
import com.practicum.pexelsapp.data.mapper.toUiModel
import com.practicum.pexelsapp.domain.usecase.GetBookmarkedPhotoByIdUseCase
import com.practicum.pexelsapp.domain.usecase.GetPhotoByIdUseCase
import com.practicum.pexelsapp.domain.usecase.IsPhotoBookmarkedUseCase
import com.practicum.pexelsapp.domain.usecase.ToggleBookmarkUseCase
import com.practicum.pexelsapp.presentation.model.PhotoUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val getPhotoByIdUseCase: GetPhotoByIdUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase,
    private val getBookmarkedPhotoByIdUseCase: GetBookmarkedPhotoByIdUseCase,
    private val isPhotoBookmarkedUseCase: IsPhotoBookmarkedUseCase,
    savedStateHandle: SavedStateHandle

) : ViewModel() {


    private val photoId: Int = checkNotNull(savedStateHandle["photoId"])

    private val _state = MutableStateFlow<DetailsState>(DetailsState.Loading)
    val state: StateFlow<DetailsState> = _state.asStateFlow()


    init {
        observeBookmarkStatus()
        processCommand(DetailsCommand.LoadPhoto)
    }

    fun processCommand(command: DetailsCommand) {
        when (command) {
            is DetailsCommand.LoadPhoto -> {
                loadPhotoData()
            }

            is DetailsCommand.ToggleBookmark -> {
                toggleBookmark(command.photo)
            }

            is DetailsCommand.Back -> {
                _state.update { DetailsState.Finished }
            }
        }
    }

    private fun loadPhotoData() {
        viewModelScope.launch {
            _state.update { DetailsState.Loading }

            val isInitiallyBookmarked = isPhotoBookmarkedUseCase(photoId).first()

            val cachedPhoto = getBookmarkedPhotoByIdUseCase(photoId)

            if (cachedPhoto != null) {
                _state.update {
                    DetailsState.Content(
                        photo = cachedPhoto.toUiModel(),
                        isBookmarked = true,
                        isDownloadEnabled = true
                    )
                }
            } else {
                getPhotoByIdUseCase(photoId).onSuccess { photo ->
                    _state.update {
                        DetailsState.Content(
                            photo = photo.toUiModel(),
                            isBookmarked = isInitiallyBookmarked,
                            isDownloadEnabled = true
                        )
                    }
                }.onFailure {
                    _state.update { DetailsState.Error }
                }
            }
        }
    }

    private fun toggleBookmark(photoUi: PhotoUiModel) {
        viewModelScope.launch {
            val currentState = _state.value
            if (currentState is DetailsState.Content) {
                toggleBookmarkUseCase(photoUi.toDomain(), currentState.isBookmarked)
            }
        }
    }

    private fun observeBookmarkStatus() {
        viewModelScope.launch {
            isPhotoBookmarkedUseCase(photoId).collect { bookmarked ->
                _state.update { currentState ->
                    if (currentState is DetailsState.Content) {
                        currentState.copy(isBookmarked = bookmarked)
                    } else {
                        currentState
                    }
                }
            }
        }
    }
}


sealed interface DetailsCommand {
    object LoadPhoto : DetailsCommand
    data class ToggleBookmark(val photo: PhotoUiModel) : DetailsCommand
    object Back : DetailsCommand
}

@Immutable
sealed interface DetailsState {
    object Loading : DetailsState
    object Error : DetailsState
    object Finished : DetailsState

    data class Content(
        val photo: PhotoUiModel,
        val isBookmarked: Boolean = false,
        val isDownloadEnabled: Boolean = false
    ) : DetailsState
}

