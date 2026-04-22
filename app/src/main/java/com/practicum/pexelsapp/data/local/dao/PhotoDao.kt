package com.practicum.pexelsapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.pexelsapp.data.local.PhotoCacheEntity
import com.practicum.pexelsapp.data.local.PhotoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: PhotoEntity)

    @Delete
    suspend fun deletePhoto(photo: PhotoEntity)

    @Query("SELECT * FROM bookmarks")
    fun getAllBookmarks(): Flow<List<PhotoEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE id = :id)")
    fun isPhotoBookmarked(id: Int): Flow<Boolean>

    @Query("SELECT * FROM bookmarks WHERE id = :id")
    suspend fun getPhotoById(id: Int): PhotoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedPhotos(photos: List<PhotoCacheEntity>)

    @Query("SELECT * FROM photos_cache")
    suspend fun getAllCachedPhotos(): List<PhotoCacheEntity>

    @Query("DELETE FROM photos_cache")
    suspend fun clearCache()
}