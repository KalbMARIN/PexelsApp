package com.practicum.pexelsapp.domain.usecase

import androidx.paging.PagingData
import com.practicum.pexelsapp.domain.entity.Photo
import com.practicum.pexelsapp.domain.repository.PhotosRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCuratedPhotosUseCase @Inject constructor(
    private val repository: PhotosRepository
) {
    operator fun invoke(): Flow<PagingData<Photo>> {
        return repository.getCuratedPhotos()
    }
}