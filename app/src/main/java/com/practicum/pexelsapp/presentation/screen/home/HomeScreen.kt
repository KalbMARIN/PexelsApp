package com.practicum.pexelsapp.presentation.screen.home

import android.widget.Toast
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.practicum.pexelsapp.R
import com.practicum.pexelsapp.presentation.components.common.ShimmerGridPlaceholder
import com.practicum.pexelsapp.presentation.components.home.HomeTopBar
import com.practicum.pexelsapp.presentation.components.home.PhotosGrid
import com.practicum.pexelsapp.presentation.components.stubs.NetworkErrorStub
import com.practicum.pexelsapp.presentation.components.stubs.NoResultsStub
import com.practicum.pexelsapp.presentation.model.PhotoUiModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onPhotoClick: (Int) -> Unit
) {

    val photos = viewModel.photos.collectAsLazyPagingItems()

    val homeState by viewModel.state.collectAsStateWithLifecycle()
    val categories by viewModel.displayCategories.collectAsStateWithLifecycle()


    val context = LocalContext.current

    val isRefreshing = photos.loadState.refresh is LoadState.Loading

    LaunchedEffect(photos.loadState.refresh) {
        if (photos.loadState.refresh is LoadState.Error && photos.itemCount > 0) {
            Toast.makeText(
                context,
                context.getString(R.string.no_internet_connection),
                Toast.LENGTH_SHORT
            ).show()
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
        queryText = homeState.searchQuery,
        isLoading = isRefreshing,
        onQueryChange = {
            viewModel.processCommand(HomeCommand.SearchQueryChanged(it))
        },
        onRetryClick = {
            viewModel.processCommand(HomeCommand.Retry)
            photos.retry()
        },
        onPhotoClick = onPhotoClick
    )
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    photos: LazyPagingItems<PhotoUiModel>,
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
                onCategoryClick = { category ->
                    onQueryChange(category)
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
                HomeUiState.Loading -> ShimmerGridPlaceholder()
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


