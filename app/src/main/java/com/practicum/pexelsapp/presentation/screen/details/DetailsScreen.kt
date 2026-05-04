package com.practicum.pexelsapp.presentation.screen.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.practicum.pexelsapp.data.util.downloadPhoto
import com.practicum.pexelsapp.presentation.components.common.LoadingIndicator
import com.practicum.pexelsapp.presentation.components.details.DetailsActionButtons
import com.practicum.pexelsapp.presentation.components.details.DetailsImageCard
import com.practicum.pexelsapp.presentation.components.details.DetailsTopBar
import com.practicum.pexelsapp.presentation.components.stubs.ImageNotFoundStub
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DetailsScreen(
    state: DetailsState,
    photographer: String,
    imageUrl: String,
    isBookmarked: Boolean,
    isDownloadEnabled: Boolean,
    onBookmarkClick: () -> Unit,
    onBackClick: () -> Unit,
    onExploreClick: () -> Unit
) {

    val currentPhotoId = (state as? DetailsState.Content)?.photo?.id ?: 0
    val context = LocalContext.current
    var isDownloadComplete by remember { mutableStateOf(false) }
    var isDownloading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            DetailsTopBar(
                photographerName = if (state is DetailsState.Error) "" else photographer,
                onBackClick = onBackClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        DetailsContent(
            paddingValues = paddingValues,
            state = state,
            imageUrl = imageUrl,
            photoId = currentPhotoId,
            isBookmarked = isBookmarked,
            isDownloadEnabled = isDownloadEnabled,
            onBookmarkClick = onBookmarkClick,
            isDownloadComplete = isDownloadComplete,
            isDownloading = isDownloading,
            onDownloadClick = {
                if (imageUrl.isNotEmpty() && isDownloadEnabled) {
                    isDownloading = true
                    downloadPhoto(
                        context = context,
                        url = imageUrl,
                        fileName = "Pexels_Photo_$currentPhotoId",
                        onDownloadFinished = {
                            isDownloading = false
                            isDownloadComplete = true
                            scope.launch {
                                delay(3000)
                                isDownloadComplete = false
                            }
                        }
                    )
                }
            },
            onExploreClick = onExploreClick
        )
    }
}

@Composable
private fun DetailsContent(
    paddingValues: PaddingValues,
    state: DetailsState,
    imageUrl: String,
    photoId: Int,
    isBookmarked: Boolean,
    isDownloadEnabled: Boolean,
    isDownloadComplete: Boolean,
    isDownloading: Boolean,
    onBookmarkClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onExploreClick: () -> Unit
) {

    if (state is DetailsState.Error) {
        ImageNotFoundStub(onExploreClick = onExploreClick)
    } else {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state is DetailsState.Loading) {
                LoadingIndicator()
            } else {
                Spacer(modifier = Modifier.height(4.dp))
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                DetailsImageCard(
                    imageUrl = imageUrl,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                DetailsActionButtons(
                    onDownloadClick = onDownloadClick,
                    onBookmarkClick = onBookmarkClick,
                    isBookmarked = isBookmarked,
                    isDownloadEnabled = isDownloadEnabled,
                    isDownloadComplete = isDownloadComplete,
                    isDownloading = isDownloading
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}