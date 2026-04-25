package com.practicum.pexelsapp.presentation.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.practicum.pexelsapp.domain.entity.Photo
import com.practicum.pexelsapp.presentation.components.common.LoadingIndicator

@Composable
fun HomeTopBar(
    modifier: Modifier = Modifier,
    queryText: String,
    categories: List<String>,
    isLoading: Boolean,
    onQueryChange: (String) -> Unit,
    onCategoryClick: (String) -> Unit
) {

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(top = 7.dp)
    ) {
        Spacer(modifier = Modifier.height(14.dp))
        SearchBar(
            query = queryText,
            onQueryChange = onQueryChange
        )

        if (isLoading && categories.isEmpty()) {
            LoadingIndicator(modifier = Modifier.padding(top = 12.dp))
        }


        Spacer(modifier = Modifier.height(24.dp))

        CategoryChips(
            categories = categories,
            selectedCategory = queryText,
            onCategoryClick = onCategoryClick
        )

        if (isLoading && categories.isNotEmpty()) {
            LoadingIndicator(modifier = Modifier.padding(top = 12.dp))
        }

        if (!isLoading) {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PhotosGrid(
    photos: LazyPagingItems<Photo>,
    onPhotoClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    showPhotographerName: Boolean = false
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(17.dp),
        verticalItemSpacing = 15.dp
    ) {
        items(
            count = photos.itemCount,
            key = { index ->
                photos.peek(index)?.id ?: index
            },
            contentType = { "photo_card" }
        ) { index ->

            val photo = photos[index]
            photo?.let {
                PhotoCard(
                    photo = it,
                    showPhotographerName = showPhotographerName,
                    onClick = { onPhotoClick(it.id) }
                )
            }
        }
    }
}

