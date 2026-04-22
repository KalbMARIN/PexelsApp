package com.practicum.pexelsapp.presentation.screen.home

import android.widget.Toast
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.practicum.pexelsapp.domain.entity.Photo
import com.practicum.pexelsapp.presentation.components.home.HomeTopBar
import com.practicum.pexelsapp.presentation.components.home.PhotosGrid
import com.practicum.pexelsapp.presentation.components.stubs.NetworkErrorStub
import com.practicum.pexelsapp.presentation.components.stubs.NoResultsStub

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onPhotoClick: (Int) -> Unit
) {

    val photos = viewModel.photos.collectAsLazyPagingItems()
    val categories by viewModel.displayCategories.collectAsState()
    val queryText by viewModel.searchQueryText.collectAsState()

    val context = LocalContext.current

    val isRefreshing = photos.loadState.refresh is LoadState.Loading

    LaunchedEffect(photos.loadState.refresh) {
        if (photos.loadState.refresh is LoadState.Error && photos.itemCount > 0) {
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
        }
    }

    val uiState = remember(photos.loadState, photos.itemCount) {
        when {
            photos.loadState.refresh is LoadState.Loading && photos.itemCount == 0 -> HomeUiState.Loading
            photos.loadState.refresh is LoadState.Error && photos.itemCount == 0 -> HomeUiState.Error
            photos.loadState.refresh is LoadState.NotLoading && photos.itemCount == 0 -> HomeUiState.Empty
            else -> HomeUiState.Success
        }
    }


    HomeContent(
        uiState = uiState,
        photos = photos,
        categories = categories,
        queryText = queryText,
        isLoading = isRefreshing,
        onQueryChange = viewModel::onSearchQueryChange,
        onRetryClick = {
            viewModel.retryAll()
            photos.retry()
        },
        onPhotoClick = onPhotoClick
    )
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    photos: LazyPagingItems<Photo>,
    categories: List<String>,
    queryText: String,
    isLoading: Boolean,
    onQueryChange: (String) -> Unit,
    onRetryClick: () -> Unit,
    onPhotoClick: (Int) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            HomeTopBar(
                queryText = queryText,
                categories = categories,
                isLoading = isLoading,
                onQueryChange = onQueryChange,
                onCategoryClick = {
                    onQueryChange(it)
                    focusManager.clearFocus()
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                }
        ) {
            when (uiState) {
                HomeUiState.Loading -> FullScreenLoading()
                HomeUiState.Empty -> NoResultsStub(onExploreClick = { onQueryChange("") })
                HomeUiState.Error -> NetworkErrorStub(onRetryClick = onRetryClick)
                HomeUiState.Success -> {

                    PhotosGrid(
                        photos = photos,
                        onPhotoClick = onPhotoClick
                    )
                }
            }
        }
    }
}

@Composable
private fun FullScreenLoading() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}
