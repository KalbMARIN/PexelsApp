package com.practicum.pexelsapp.presentation.screen.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.practicum.pexelsapp.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    isDataLoaded: Boolean,
    onFinished: () -> Unit
) {
    val alphaAnim = remember { Animatable(0f) }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primary)
            .windowInsetsPadding(WindowInsets(0, 0, 0, 0)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_logo_pexels),
            contentDescription = stringResource(R.string.logo_pexels),
            modifier = Modifier
                .size(280.dp)
                .graphicsLayer {
                    alpha = alphaAnim.value
                }
        )
    }

    LaunchedEffect(Unit) {
        delay(100)
        alphaAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            )
        )
    }

    LaunchedEffect(isDataLoaded) {
        if (isDataLoaded) {
            delay(1000)
            onFinished()
        }
    }
}
