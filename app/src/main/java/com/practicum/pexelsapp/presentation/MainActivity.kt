package com.practicum.pexelsapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.practicum.pexelsapp.presentation.navigation.Screen
import com.practicum.pexelsapp.presentation.navigation.components.BottomNavBar
import com.practicum.pexelsapp.presentation.screen.bookmark.BookmarksScreen
import com.practicum.pexelsapp.presentation.screen.bookmark.BookmarksViewModel
import com.practicum.pexelsapp.presentation.screen.details.DetailsScreen
import com.practicum.pexelsapp.presentation.screen.details.DetailsState
import com.practicum.pexelsapp.presentation.screen.details.DetailsViewModel
import com.practicum.pexelsapp.presentation.screen.home.HomeScreen
import com.practicum.pexelsapp.presentation.screen.home.HomeViewModel
import com.practicum.pexelsapp.presentation.screen.splash.SplashScreen
import com.practicum.pexelsapp.presentation.screen.splash.SplashViewModel
import com.practicum.pexelsapp.presentation.ui.theme.PexelsAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                !splashViewModel.isReady.value
            }
        }
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = false // белые иконки
        }

        setContent {
            PexelsAppTheme {
                Surface(color = MaterialTheme.colorScheme.background) {

                    val isDataReady by splashViewModel.isReady.collectAsState()
                    val navController = rememberNavController()

                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route


                    val showBottomBar =
                        currentRoute in listOf(Screen.Home.route, Screen.Bookmarks.route)

                    Scaffold(
                        bottomBar = {
                            if (showBottomBar) {
                                BottomNavBar(navController = navController)
                            }
                        }
                    ) { _ ->

                        NavHost(
                            navController = navController,
                            startDestination = Screen.Splash.route,
                            modifier = Modifier
                                .fillMaxSize()
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

                                val state by detailsViewModel.state.collectAsState()
                                val isSaved by detailsViewModel.isBookmarked.collectAsState()

                                DetailsScreen(
                                    photoId = it.arguments?.getInt("photoId") ?: 0,
                                    state = state,
                                    photographer = (state as? DetailsState.Success)?.photo?.photographer
                                        ?: "",
                                    imageUrl = (state as? DetailsState.Success)?.photo?.largeUrl
                                        ?: "",
                                    isBookmarked = isSaved,
                                    onBookmarkClick = {
                                        (state as? DetailsState.Success)?.let { success ->
                                            detailsViewModel.toggleBookmark(success.photo)
                                        }
                                    },
                                    onBackClick = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}





