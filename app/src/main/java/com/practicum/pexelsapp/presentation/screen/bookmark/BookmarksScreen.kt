package com.practicum.pexelsapp.presentation.screen.bookmark

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.practicum.pexelsapp.presentation.components.bookmarks.BookmarksEmptyState
import com.practicum.pexelsapp.presentation.components.bookmarks.BookmarksGrid
import com.practicum.pexelsapp.presentation.components.bookmarks.BookmarksTopBar
import com.practicum.pexelsapp.presentation.components.common.ShimmerGridPlaceholder
import com.practicum.pexelsapp.presentation.model.PhotoUiModel

@Composable
fun BookmarksScreen(
    viewModel: BookmarksViewModel,
    onExploreClick: () -> Unit,
    onPhotoClick: (Int) -> Unit
) {

    val pagingPhotos = viewModel.bookmarks.collectAsLazyPagingItems()

    val isLoading = pagingPhotos.loadState.refresh is LoadState.Loading

    val isEmpty =
        pagingPhotos.loadState.refresh is LoadState.NotLoading && pagingPhotos.itemCount == 0



    BookmarksContent(
        photos = pagingPhotos,
        isLoading = isLoading,
        isEmpty = isEmpty,
        onExploreClick = {
            viewModel.processCommand(BookmarksCommand.ExploreClicked)
            onExploreClick()
        },
        onPhotoClick = { id ->
            viewModel.processCommand(BookmarksCommand.PhotoClicked(id))
            onPhotoClick(id)
        }
    )
}

@Composable
private fun BookmarksContent(
    photos: LazyPagingItems<PhotoUiModel>,
    isLoading: Boolean,
    isEmpty: Boolean,
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
                .padding(bottom = 64.dp)
        ) {

            if (isLoading) {
                ShimmerGridPlaceholder()
            }


            if (isEmpty) {
                BookmarksEmptyState(onExploreClick = onExploreClick)
            } else if (!isLoading) {

                BookmarksGrid(
                    photos = photos,
                    onPhotoClick = onPhotoClick,
                    modifier = Modifier.padding(top = 20.dp)
                )
            }
        }
    }
}