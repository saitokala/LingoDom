package com.lingodom.app.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Game : Screen("game/{roundNumber}") {
        fun createRoute(roundNumber: Int) = "game/$roundNumber"
    }
    data object Stats : Screen("stats")
    data object Settings : Screen("settings")
}
