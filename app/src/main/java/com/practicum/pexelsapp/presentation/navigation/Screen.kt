package com.practicum.pexelsapp.presentation.navigation

sealed class Screen(val route: String) {

    object Splash : Screen("splash_screen")
    object Home : Screen("home_screen")
    object Bookmarks : Screen("bookmarks_screen")
    object Details : Screen("details_screen/{photoId}") {
        fun passId(id: Int) = "details_screen/$id"
    }
}