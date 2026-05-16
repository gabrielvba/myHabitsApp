package com.habitos.androidApp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.habitos.androidApp.ui.create.CreateHabitScreen
import com.habitos.androidApp.ui.create.CreateHabitViewModel
import com.habitos.androidApp.ui.detail.HabitDetailScreen
import com.habitos.androidApp.ui.detail.HabitDetailViewModel
import com.habitos.androidApp.ui.habits.HabitListScreen
import com.habitos.androidApp.ui.habits.HabitListViewModel
import com.habitos.domain.usecase.*

@Composable
fun AppNavigation(
    getTodayHabitsUseCase: GetTodayHabitsUseCase,
    completeHabitUseCase: CompleteHabitUseCase,
    uncompleteHabitUseCase: UncompleteHabitUseCase,
    createHabitUseCase: CreateHabitUseCase,
    getHabitStreakUseCase: GetHabitStreakUseCase,
    getHabitHistoryUseCase: GetHabitHistoryUseCase,
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            val viewModel = remember {
                HabitListViewModel(getTodayHabitsUseCase, completeHabitUseCase, uncompleteHabitUseCase)
            }
            // Workaround logic: Always refresh when entering screen
            viewModel.loadHabits()

            HabitListScreen(
                viewModel = viewModel,
                onCreateClick = { navController.navigate("create") },
                onHabitClick = { habitId -> navController.navigate("detail/\$habitId") }
            )
        }

        composable("create") {
            val viewModel = remember {
                CreateHabitViewModel(createHabitUseCase)
            }
            CreateHabitScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        composable(
            route = "detail/{habitId}",
            arguments = listOf(navArgument("habitId") { type = NavType.StringType })
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getString("habitId") ?: return@composable
            val viewModel = remember(habitId) {
                HabitDetailViewModel(habitId, getHabitStreakUseCase, getHabitHistoryUseCase)
            }
            HabitDetailScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
