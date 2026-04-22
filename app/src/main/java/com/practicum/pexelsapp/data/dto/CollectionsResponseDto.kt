package com.practicum.pexelsapp.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@kotlinx.serialization.Serializable
data class CollectionsResponseDto(
    @SerialName("collections") val collections: List<CollectionItemDto>
)

@Serializable
data class CollectionItemDto(
    @SerialName("title") val title: String
)