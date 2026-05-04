package com.practicum.pexelsapp.presentation.screen.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.pexelsapp.domain.usecase.GetCuratedPhotosUseCase
import com.practicum.pexelsapp.domain.usecase.GetFeaturedCollectionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getFeaturedCollectionsUseCase: GetFeaturedCollectionsUseCase,
    private val getCuratedPhotosUseCase: GetCuratedPhotosUseCase
) : ViewModel() {

    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()

            try {
                coroutineScope {
                    val collectionsDeferred = async { getFeaturedCollectionsUseCase() }
                    val photosDeferred = async { getCuratedPhotosUseCase().first() }

                    awaitAll(collectionsDeferred, photosDeferred)
                }
            } catch (e: Exception) {
                Log.e("Splash", "Failed to fetch collections", e)
            } finally {
                val elapsedTime = System.currentTimeMillis() - startTime
                val minDisplayTime = 1200L

                if (elapsedTime < minDisplayTime) {
                    delay(minDisplayTime - elapsedTime)
                }

                _isReady.value = true
            }
        }
    }
}