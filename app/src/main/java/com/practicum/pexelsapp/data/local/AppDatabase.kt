package com.practicum.pexelsapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.pexelsapp.data.local.dao.PhotoDao

@Database(
    entities = [
        PhotoEntity::class,
        PhotoCacheEntity::class,
        CollectionEntity::class
               ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun photoDao(): PhotoDao
}