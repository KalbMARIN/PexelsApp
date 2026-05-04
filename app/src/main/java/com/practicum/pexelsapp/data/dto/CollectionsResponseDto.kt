package com.practicum.pexelsapp.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CollectionsResponseDto(
    @SerialName("collections") val collections: List<CollectionItemDto>
)

@Serializable
data class CollectionItemDto(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String
)