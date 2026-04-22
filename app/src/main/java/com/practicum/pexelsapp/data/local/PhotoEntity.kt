package com.practicum.pexelsapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class PhotoEntity(
    @PrimaryKey val id: Int,
    val url: String,
    val largeUrl: String,
    val photographer: String,
    val alt: String,
    val photographerUrl: String,
    val isBookmarked: Boolean = true
)

@Entity(tableName = "photos_cache")
data class PhotoCacheEntity(
    @PrimaryKey val id: Int,
    val url: String,
    val largeUrl: String,
    val photographer: String,
    val timestamp: Long = System.currentTimeMillis()
)
