package com.practicum.pexelsapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.practicum.pexelsapp.data.local.CollectionEntity
import com.practicum.pexelsapp.data.local.dao.PhotoDao
import com.practicum.pexelsapp.data.mapper.toDomain
import com.practicum.pexelsapp.data.mapper.toEntity
import com.practicum.pexelsapp.data.remote.PexelsApi
import com.practicum.pexelsapp.data.remote.PhotoPagingSource
import com.practicum.pexelsapp.domain.entity.Photo
import com.practicum.pexelsapp.domain.repository.PhotosRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PhotosRepositoryImpl @Inject constructor(
    private val api: PexelsApi,
    private val photoDao: PhotoDao
) : PhotosRepository {
    override fun getCuratedPhotos(): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(
                pageSize = 30,
                initialLoadSize = 30,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PhotoPagingSource(api = api, photoDao, query = null) }
        ).flow
            .map { pagingData -> pagingData.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)
    }

    override fun searchPhotos(query: String): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(
                pageSize = 30,
                initialLoadSize = 30,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PhotoPagingSource(api = api, photoDao, query = query) }
        ).flow
            .map { pagingData -> pagingData.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun getFeaturedCollections(): Result<List<String>> = withContext(Dispatchers.IO) {
        try {
            val cachedCollections = photoDao.getAllCollections().take(1).firstOrNull() ?: emptyList()

            if (cachedCollections.isNotEmpty()) {
                val cacheTimestamp = cachedCollections.maxOf { it.timestamp }
                val currentTime = System.currentTimeMillis()
                val oneHour = 3600_000

                if (currentTime - cacheTimestamp < oneHour) {
                    return@withContext Result.success(cachedCollections.map { it.title })
                }
            }

            val response = api.getFeaturedCollections()
            val entities = response.collections.map { dto ->
                CollectionEntity(id = dto.id, title = dto.title)
            }

            photoDao.insertCollections(entities)

            Result.success(entities.map { it.title })

        } catch (e: Exception) {
            val fallbackCollections = photoDao.getAllCollections().take(1).firstOrNull() ?: emptyList()
            if (fallbackCollections.isNotEmpty()) {
                Result.success(fallbackCollections.map { it.title })
            } else {
                Result.failure(e)
            }
        }
    }

    override fun getCollectionsFlow(): Flow<List<String>> {
        return photoDao.getAllCollections()
            .map { list -> list.map { it.title } }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun getPhotoById(id: Int): Result<Photo> {
        return try {
            val photoDto = api.getPhotoById(id)
            Result.success(photoDto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getBookmarkedPhotoById(id: Int): Photo? {
        val entity = photoDao.getPhotoById(id)
        return entity?.toDomain()
    }

    override fun getBookmarks(): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(
                pageSize = 30,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { photoDao.getAllBookmarksPagingSource() }
        ).flow
            .map { pagingData ->
                pagingData.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun saveToBookmarks(photo: Photo) {
        photoDao.insertPhoto(photo.toEntity())
    }

    override suspend fun deleteFromBookmarks(photo: Photo) {
        photoDao.deletePhoto(photo.toEntity())
    }

    override fun isPhotoBookmarked(id: Int): Flow<Boolean> {
        return photoDao.isPhotoBookmarked(id)
    }
}