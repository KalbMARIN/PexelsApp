package com.practicum.pexelsapp.presentation.components.details

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import coil3.compose.AsyncImage
import com.practicum.pexelsapp.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DetailsTopBar(
    photographerName: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = 14.dp)
            .height(64.dp)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.CenterStart
    ) {

        BackButton(onClick = onBackClick)

        Text(
            text = photographerName,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun BackButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_back_arrow),
            contentDescription = stringResource(R.string.back_btn),
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun DetailsImageCard(
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(1f) }

    val animatedScale by animateFloatAsState(
        targetValue = scale,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy),
        label = "ScaleAnimation"
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTransformGestures { _, _, zoom, _ ->
                    scale = (scale * zoom).coerceIn(1f, 4f)
                }
            }
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        if (event.changes.all { !it.pressed }) {
                            scale = 1f
                        }
                    }
                }
            }
    ) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_photo),
                contentDescription = stringResource(R.string.placeholder_details),
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            )

            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = animatedScale,
                        scaleY = animatedScale
                    ),
            )
        }
    }
}

@Composable
fun DetailsActionButtons(
    modifier: Modifier = Modifier,
    onDownloadClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    isBookmarked: Boolean = false
) {

    val scope = rememberCoroutineScope()
    var isDownloadComplete by remember { mutableStateOf(false) }

    val iconOffset by animateDpAsState(
        targetValue = if (isDownloadComplete) 140.dp else 0.dp,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "DownloadAnim"
    )

    val textAlpha by animateFloatAsState(
        targetValue = if (isDownloadComplete) 0f else 1f,
        animationSpec = tween(durationMillis = 250),
        label = "TextFade"
    )

    val okTextAlpha by animateFloatAsState(
        targetValue = if (isDownloadComplete) 1f else 0f,
        animationSpec = tween(durationMillis = 250, delayMillis = 200),
        label = "OkTextFade"
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.52f)
                .height(48.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(24.dp)
                )
                .clip(RoundedCornerShape(24.dp))
                .clickable {
                    if (!isDownloadComplete) {
                        onDownloadClick()
                        isDownloadComplete = true
                        scope.launch {
                            delay(3000)
                            isDownloadComplete = false
                        }
                    }
                }
        ) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 66.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = if (isDownloadComplete) stringResource(R.string.ok_download_btn) else stringResource(
                        R.string.download_btn
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = textAlpha),
                    modifier = Modifier.offset(x = (1f - textAlpha) * (-20).dp)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 66.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = stringResource(R.string.ok_download_btn),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = okTextAlpha),
                    modifier = Modifier.offset(x = (1f - okTextAlpha) * 20.dp)
                )
            }

            Box(
                modifier = Modifier
                    .offset(x = iconOffset)
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Crossfade(
                    targetState = isDownloadComplete,
                    animationSpec = tween(300),
                    label = "IconCrossfade"
                ) { complete ->
                    if (complete) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Done",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Icon(
                            painter = painterResource(
                                id = R.drawable.ic_download
                            ),
                            contentDescription = stringResource(R.string.download_photo),
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                }

            }
        }

        BookmarkButton(isBookmarked = isBookmarked, onClick = onBookmarkClick)

    }
}

@Composable
private fun BookmarkButton(isBookmarked: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(MaterialTheme.colorScheme.surface, CircleShape)
            .clip(CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(
                id = if (isBookmarked) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark_border
            ),
            contentDescription = stringResource(R.string.add_to_bookmarks),
            tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(20.dp)
        )
    }
}
