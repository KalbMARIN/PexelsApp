package com.practicum.pexelsapp.domain.usecase

import com.practicum.pexelsapp.domain.entity.Photo
import com.practicum.pexelsapp.domain.repository.PhotosRepository
import javax.inject.Inject

class GetBookmarkedPhotoByIdUseCase @Inject constructor(
    private val repository: PhotosRepository
) {
    suspend operator fun invoke(id: Int): Photo? {
        return repository.getBookmarkedPhotoById(id)
    }
}