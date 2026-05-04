package com.practicum.pexelsapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.practicum.pexelsapp.presentation.screen.bookmark.BookmarksScreen
import com.practicum.pexelsapp.presentation.screen.bookmark.BookmarksViewModel
import com.practicum.pexelsapp.presentation.screen.details.DetailsCommand
import com.practicum.pexelsapp.presentation.screen.details.DetailsScreen
import com.practicum.pexelsapp.presentation.screen.details.DetailsState
import com.practicum.pexelsapp.presentation.screen.details.DetailsViewModel
import com.practicum.pexelsapp.presentation.screen.home.HomeScreen
import com.practicum.pexelsapp.presentation.screen.home.HomeViewModel
import com.practicum.pexelsapp.presentation.screen.splash.SplashScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    isDataReady: Boolean,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(route = Screen.Splash.route) {

            SplashScreen(
                isReady = isDataReady,
                onFinished = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.Home.route) {
            val homeViewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                viewModel = homeViewModel,
                onPhotoClick = { photoId ->
                    navController.navigate(Screen.Details.passId(photoId))

                }
            )
        }

        composable(route = Screen.Bookmarks.route) {
            val bookmarksViewModel: BookmarksViewModel = hiltViewModel()

            BookmarksScreen(
                viewModel = bookmarksViewModel,
                onExploreClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onPhotoClick = { id ->
                    navController.navigate(Screen.Details.passId(id))
                }
            )
        }

        composable(
            route = "details_screen/{photoId}",
            arguments = listOf(navArgument("photoId") {
                type = NavType.IntType
            })
        ) {
            val detailsViewModel: DetailsViewModel = hiltViewModel()

            val state by detailsViewModel.state.collectAsStateWithLifecycle()

            LaunchedEffect(state) {
                if (state is DetailsState.Finished) {
                    navController.popBackStack()
                }
            }

            DetailsScreen(
                state = state,
                photographer = (state as? DetailsState.Content)?.photo?.photographer ?: "",
                imageUrl = (state as? DetailsState.Content)?.photo?.largeUrl ?: "",
                isBookmarked = (state as? DetailsState.Content)?.isBookmarked ?: false,
                isDownloadEnabled = (state as? DetailsState.Content)?.isDownloadEnabled ?: false,
                onBookmarkClick = {
                    (state as? DetailsState.Content)?.let { content ->
                        detailsViewModel.processCommand(DetailsCommand.ToggleBookmark(content.photo))
                    }
                },
                onBackClick = { detailsViewModel.processCommand(DetailsCommand.Back) },
                onExploreClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
    }
}