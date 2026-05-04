package com.practicum.pexelsapp.domain.usecase

import com.practicum.pexelsapp.domain.repository.PhotosRepository
import javax.inject.Inject

class IsPhotoBookmarkedUseCase @Inject constructor(
    private val repository: PhotosRepository
) {
    operator fun invoke(id: Int) = repository.isPhotoBookmarked(id)
}