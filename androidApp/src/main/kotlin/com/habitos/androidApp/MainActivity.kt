package com.habitos.androidApp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.habitos.androidApp.ui.AppNavigation
import com.habitos.data.db.DatabaseDriverFactory
import com.habitos.data.db.HabitosDatabase
import com.habitos.data.repository.CompletionRepositoryImpl
import com.habitos.data.repository.HabitRepositoryImpl
import com.habitos.domain.usecase.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Manual Dependency Injection for Phase 1
        val driver = DatabaseDriverFactory(this).createDriver()
        val database = HabitosDatabase(driver)

        val habitRepository = HabitRepositoryImpl(database)
        val completionRepository = CompletionRepositoryImpl(database)

        val getHabitStreakUseCase = GetHabitStreakUseCase(habitRepository, completionRepository)
        val getTodayHabitsUseCase = GetTodayHabitsUseCase(habitRepository, completionRepository, getHabitStreakUseCase)
        val completeHabitUseCase = CompleteHabitUseCase(completionRepository)
        val uncompleteHabitUseCase = UncompleteHabitUseCase(completionRepository)
        val createHabitUseCase = CreateHabitUseCase(habitRepository)
        val getHabitHistoryUseCase = GetHabitHistoryUseCase(completionRepository)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        getTodayHabitsUseCase = getTodayHabitsUseCase,
                        completeHabitUseCase = completeHabitUseCase,
                        uncompleteHabitUseCase = uncompleteHabitUseCase,
                        createHabitUseCase = createHabitUseCase,
                        getHabitStreakUseCase = getHabitStreakUseCase,
                        getHabitHistoryUseCase = getHabitHistoryUseCase
                    )
                }
            }
        }
    }
}
