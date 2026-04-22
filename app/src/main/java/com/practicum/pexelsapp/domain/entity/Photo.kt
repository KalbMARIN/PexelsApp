package com.practicum.pexelsapp.domain.entity

data class Photo(
    val id: Int,
    val url: String,
    val largeUrl: String,
    val photographer: String,
    val alt: String,
    val photographerUrl: String
)
