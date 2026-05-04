package com.practicum.pexelsapp.domain.usecase

import com.practicum.pexelsapp.domain.entity.Photo
import com.practicum.pexelsapp.domain.repository.PhotosRepository
import javax.inject.Inject

class GetPhotoByIdUseCase @Inject constructor(
    private val repository: PhotosRepository
) {

    suspend operator fun invoke(id: Int): Result<Photo> {
        return repository.getPhotoById(id)
    }
}