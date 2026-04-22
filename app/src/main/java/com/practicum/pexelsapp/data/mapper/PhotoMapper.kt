package com.practicum.pexelsapp.data.mapper

import com.practicum.pexelsapp.data.dto.PhotoDto
import com.practicum.pexelsapp.data.dto.PhotoSourcesDto
import com.practicum.pexelsapp.data.local.PhotoCacheEntity
import com.practicum.pexelsapp.data.local.PhotoEntity
import com.practicum.pexelsapp.domain.entity.Photo

fun PhotoDto.toCacheEntity(): PhotoCacheEntity {
    return PhotoCacheEntity(
        id = this.id,
        url = this.src.large,
        largeUrl = this.src.large2x,
        photographer = this.photographer,
        timestamp = System.currentTimeMillis()
    )
}

fun PhotoCacheEntity.toDomain(): Photo {
    return Photo(
        id = id,
        url = url,
        largeUrl = largeUrl,
        photographer = photographer,
        alt = "",
        photographerUrl = ""
    )
}

fun PhotoCacheEntity.toDto(): PhotoDto {
    return PhotoDto(
        id = id,
        width = 0,
        height = 0,
        photographer = photographer,
        url = "",
        photographerUrl = "",
        avgColor = "",
        alt = "",
        src = PhotoSourcesDto(
            original = url,
            large2x = largeUrl,
            large = url,
            medium = url,
            small = url,
            portrait = url,
            landscape = url,
            tiny = url
        )
    )
}

fun Photo.toEntity(): PhotoEntity {
    return PhotoEntity(
        id = id,
        url = url,
        largeUrl = largeUrl,
        photographer = photographer,
        alt = alt,
        photographerUrl = photographerUrl
    )
}

fun PhotoEntity.toDomain(): Photo {
    return Photo(
        id = id,
        url = url,
        largeUrl = largeUrl,
        photographer = photographer,
        alt = alt,
        photographerUrl = photographerUrl
    )
}

fun PhotoDto.toDomain(): Photo {
    return Photo(
        id = this.id,
        url = this.src.large,
        largeUrl = this.src.large2x,
        photographer = this.photographer,
        alt = this.alt,
        photographerUrl = this.photographerUrl
    )
}

