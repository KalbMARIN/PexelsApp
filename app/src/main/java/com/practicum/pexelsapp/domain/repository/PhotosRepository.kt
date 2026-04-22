package com.practicum.pexelsapp.domain.repository

import androidx.paging.PagingData
import com.practicum.pexelsapp.domain.entity.Photo
import kotlinx.coroutines.flow.Flow

interface PhotosRepository {

    fun getCuratedPhotos(): Flow<PagingData<Photo>>

    fun searchPhotos(query: String): Flow<PagingData<Photo>>

    suspend fun getFeaturedCollections(): Result<List<String>>

    suspend fun getPhotoById(id: Int): Result<Photo>

    fun getBookmarks(): Flow<List<Photo>>

    suspend fun saveToBookmarks(photo: Photo)

    suspend fun deleteFromBookmarks(photo: Photo)

    fun isPhotoBookmarked(id: Int): Flow<Boolean>
}