package com.lingodom.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.lingodom.app.core.model.GameRound
import com.lingodom.app.ui.screens.GameScreen
import com.lingodom.app.ui.screens.HomeScreen
import com.lingodom.app.ui.screens.SettingsScreen
import com.lingodom.app.ui.screens.StatsScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {

        composable(Screen.Home.route) {
            HomeScreen(
                onPlayRound = { round ->
                    navController.navigate(Screen.Game.createRoute(round.roundNumber))
                },
                onNavigateToStats = { navController.navigate(Screen.Stats.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(
            route = Screen.Game.route,
            arguments = listOf(navArgument("roundNumber") { type = NavType.IntType })
        ) { entry ->
            val roundNum = entry.arguments?.getInt("roundNumber") ?: 1
            val round = GameRound.fromRoundNumber(roundNum)
            GameScreen(
                round = round,
                onNavigateBack = { navController.popBackStack() },
                onPlayAgain = { nextRound ->
                    navController.popBackStack()
                    navController.navigate(Screen.Game.createRoute(nextRound.roundNumber))
                }
            )
        }

        composable(Screen.Stats.route) {
            StatsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.Settings.route) {
            SettingsScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
