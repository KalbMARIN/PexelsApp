package com.practicum.pexelsapp.domain.usecase

import com.practicum.pexelsapp.domain.repository.PhotosRepository
import javax.inject.Inject

class GetBookmarksUseCase @Inject constructor(
    private val repository: PhotosRepository
) {

    operator fun invoke() = repository.getBookmarks()
}