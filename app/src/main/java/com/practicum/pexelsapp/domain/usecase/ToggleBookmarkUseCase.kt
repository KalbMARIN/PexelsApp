package com.practicum.pexelsapp.domain.usecase

import com.practicum.pexelsapp.domain.entity.Photo
import com.practicum.pexelsapp.domain.repository.PhotosRepository
import javax.inject.Inject

class ToggleBookmarkUseCase @Inject constructor(
    private val repository: PhotosRepository
) {
    suspend operator fun invoke(photo: Photo, isBookmarked: Boolean) {
        if (isBookmarked) {
            repository.deleteFromBookmarks(photo)
        } else {
            repository.saveToBookmarks(photo)
        }
    }
}