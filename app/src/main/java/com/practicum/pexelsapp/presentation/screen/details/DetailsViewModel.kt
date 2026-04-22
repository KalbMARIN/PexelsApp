package com.practicum.pexelsapp.presentation.screen.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.pexelsapp.domain.entity.Photo
import com.practicum.pexelsapp.domain.usecase.GetPhotoByIdUseCase
import com.practicum.pexelsapp.domain.usecase.IsPhotoBookmarkedUseCase
import com.practicum.pexelsapp.domain.usecase.ToggleBookmarkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val getPhotoByIdUseCase: GetPhotoByIdUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase,
    isPhotoBookmarkedUseCase: IsPhotoBookmarkedUseCase,
    savedStateHandle: SavedStateHandle

) : ViewModel() {

    private val photoId: Int = checkNotNull(savedStateHandle["photoId"])

    private val _state = MutableStateFlow<DetailsState>(DetailsState.Loading)
    val state: StateFlow<DetailsState> = _state.asStateFlow()

    val isBookmarked = isPhotoBookmarkedUseCase(photoId)
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    init {
        loadPhoto()
    }
    private fun loadPhoto() {
        viewModelScope.launch {
            _state.value = DetailsState.Loading
            getPhotoByIdUseCase(photoId).onSuccess { photo ->
                _state.value = DetailsState.Success(photo)
            }.onFailure {
                _state.value = DetailsState.Error
            }
        }
    }

    fun toggleBookmark(photo: Photo) {
        viewModelScope.launch {
            toggleBookmarkUseCase(photo, isBookmarked.value)
        }
    }
}




sealed class DetailsState {
    object Loading : DetailsState()
    data class Success(val photo: Photo) : DetailsState()
    object Error : DetailsState()
}

