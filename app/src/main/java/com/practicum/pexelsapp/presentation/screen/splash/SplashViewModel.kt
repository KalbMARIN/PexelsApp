package com.practicum.pexelsapp.presentation.screen.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.pexelsapp.domain.usecase.GetCuratedPhotosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getCuratedPhotosUseCase: GetCuratedPhotosUseCase
) : ViewModel() {

    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()

    init {
        viewModelScope.launch {

            val startTime = System.currentTimeMillis()

            try {
                getCuratedPhotosUseCase().first()
            } catch (e: Exception) {
                Log.e("Splash", "Error prefetching", e)
            } finally {
                val elapsedTime = System.currentTimeMillis() - startTime
                val remainingTime = 2000L - elapsedTime
                if (remainingTime > 0) delay(remainingTime)

                _isReady.value = true
            }
        }
    }
}