package com.practicum.pexelsapp.domain.usecase

import com.practicum.pexelsapp.domain.repository.PhotosRepository
import javax.inject.Inject

class GetFeaturedCollectionsUseCase @Inject constructor(
    private val repository: PhotosRepository
) {
    suspend operator fun invoke(): Result<List<String>> {
        return repository.getFeaturedCollections()
    }
}