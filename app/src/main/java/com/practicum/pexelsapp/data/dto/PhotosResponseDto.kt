package com.practicum.pexelsapp.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotosResponseDto(
    @SerialName("photos") val photos: List<PhotoDto>,
    @SerialName("next_page") val nextPage: String? = null
)
