package com.practicum.pexelsapp.presentation.screen.bookmark

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.practicum.pexelsapp.domain.entity.Photo
import com.practicum.pexelsapp.presentation.components.bookmarks.BookmarksEmptyState
import com.practicum.pexelsapp.presentation.components.bookmarks.BookmarksGrid
import com.practicum.pexelsapp.presentation.components.bookmarks.BookmarksTopBar
import com.practicum.pexelsapp.presentation.components.common.LoadingIndicator

@Composable
fun BookmarksScreen(
    viewModel: BookmarksViewModel,
    onExploreClick: () -> Unit,
    onPhotoClick: (Int) -> Unit
) {
    val photos by viewModel.bookmarks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()


    BookmarksContent(
        photos = photos,
        isLoading = isLoading,
        onExploreClick = onExploreClick,
        onPhotoClick = onPhotoClick
    )
}

@Composable
private fun BookmarksContent(
    photos: List<Photo>,
    isLoading: Boolean,
    onExploreClick: () -> Unit,
    onPhotoClick: (Int) -> Unit
) {

    Scaffold(
        topBar = { BookmarksTopBar() },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            if (isLoading) {
                LoadingIndicator()
            } else {
                Spacer(modifier = Modifier.height(0.dp))
            }


            if (photos.isEmpty() && !isLoading) {
                BookmarksEmptyState(onExploreClick = onExploreClick)
            } else {

                BookmarksGrid(
                    photos = photos,
                    onPhotoClick = onPhotoClick,
                    modifier = Modifier.padding(top = 20.dp)
                )
            }
        }
    }
}