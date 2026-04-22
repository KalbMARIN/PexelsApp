package com.practicum.pexelsapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.practicum.pexelsapp.data.local.dao.PhotoDao
import com.practicum.pexelsapp.data.mapper.toDomain
import com.practicum.pexelsapp.data.mapper.toEntity
import com.practicum.pexelsapp.data.remote.PexelsApi
import com.practicum.pexelsapp.data.remote.PhotoPagingSource
import com.practicum.pexelsapp.domain.entity.Photo
import com.practicum.pexelsapp.domain.repository.PhotosRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    override fun searchPhotos(query: String): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(
                pageSize = 30,
                initialLoadSize = 30,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PhotoPagingSource(api = api, photoDao, query = query) }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    override suspend fun getFeaturedCollections(): Result<List<String>> {
        return try {
            val response = api.getFeaturedCollections()
            Result.success(response.collections.map { it.title })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPhotoById(id: Int): Result<Photo> {
        return try {
            val photoDto = api.getPhotoById(id)
            Result.success(photoDto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getBookmarks(): Flow<List<Photo>> {
        return photoDao.getAllBookmarks().map { list ->
            list.map { it.toDomain() }
        }
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