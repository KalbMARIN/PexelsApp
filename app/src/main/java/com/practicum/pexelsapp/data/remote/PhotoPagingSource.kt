package com.practicum.pexelsapp.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.practicum.pexelsapp.data.dto.PhotoDto
import com.practicum.pexelsapp.data.local.dao.PhotoDao
import com.practicum.pexelsapp.data.mapper.toCacheEntity
import com.practicum.pexelsapp.data.mapper.toDto

class PhotoPagingSource(
    private val api: PexelsApi,
    private val dao: PhotoDao,
    private val query: String? = null
) : PagingSource<Int, PhotoDto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PhotoDto> {

        val position = params.key ?: 1

        return try {

            val response = if (query.isNullOrEmpty()) {
                api.getCuratedPhotos(page = position, perPage = params.loadSize)
            } else {
                api.searchPhotos(query = query, page = position, perPage = params.loadSize)
            }

            val photos = response.photos

            if (query.isNullOrEmpty() && position == 1) {
                dao.clearCache() // Очищаем старый кэш
                dao.insertCachedPhotos(photos.map { it.toCacheEntity() })
            }

            LoadResult.Page(
                data = photos,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (photos.isEmpty()) null else position + 1
            )
        } catch (exception: Exception) {

            if (query.isNullOrEmpty() && position == 1) {
                val cachedPhotos = dao.getAllCachedPhotos()
                if (cachedPhotos.isNotEmpty()) {
                    val cacheTimestamp = cachedPhotos.first().timestamp
                    val currentTime = System.currentTimeMillis()
                    val oneHour = 3600_000

                    if (currentTime - cacheTimestamp < oneHour) {
                        return LoadResult.Page(
                            data = cachedPhotos.map { it.toDto() },
                            prevKey = null,
                            nextKey = null
                        )
                    } else {
                        dao.clearCache()
                    }
                }
            }
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PhotoDto>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}