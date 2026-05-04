package com.practicum.pexelsapp.presentation.model

import androidx.compose.runtime.Immutable

@Immutable
data class PhotoUiModel(
    val id: Int,
    val url: String,
    val largeUrl: String,
    val photographer: String,
    val alt: String,
    val photographerNameWithPrefix: String = "By $photographer"
)