package com.practicum.pexelsapp.presentation.components.home

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.practicum.pexelsapp.R
import com.practicum.pexelsapp.domain.entity.Photo

@Composable
fun PhotoCard(
    modifier: Modifier = Modifier,
    photo: Photo,
    showPhotographerName: Boolean = false,
    onClick: () -> Unit
) {

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val elevation by animateDpAsState(
        targetValue = if (isPressed) 4.dp else 0.dp, // Приподнимаем на 12dp при нажатии
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy),
        label = "ElevationAnimation"
    )

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier.fillMaxWidth(),
        interactionSource = interactionSource,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp,
            pressedElevation = elevation
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {

        Box(
            contentAlignment = Alignment.BottomCenter
        ) {

            AsyncImage(
                model = photo.url,
                contentDescription = photo.alt,
                contentScale = ContentScale.FillWidth, // Важно для сетки
                modifier = Modifier
                    .fillMaxWidth(),
                placeholder = painterResource(id = R.drawable.placeholder)
            )

            if (showPhotographerName) {
                PhotographerOverlay(name = photo.photographer)
            }
        }
    }
}

@Composable
private fun PhotographerOverlay(name: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.4f))
            .padding(vertical = 8.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}