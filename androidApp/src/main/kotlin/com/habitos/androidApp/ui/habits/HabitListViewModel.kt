package com.habitos.androidApp.ui.habits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitos.domain.model.HabitWithStatus
import com.habitos.domain.usecase.CompleteHabitUseCase
import com.habitos.domain.usecase.GetTodayHabitsUseCase
import com.habitos.domain.usecase.UncompleteHabitUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class HabitListState(
    val habits: List<HabitWithStatus> = emptyList(),
    val isLoading: Boolean = true
)

class HabitListViewModel(
    private val getTodayHabitsUseCase: GetTodayHabitsUseCase,
    private val completeHabitUseCase: CompleteHabitUseCase,
    private val uncompleteHabitUseCase: UncompleteHabitUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HabitListState())
    val state: StateFlow<HabitListState> = _state.asStateFlow()

    init {
        loadHabits()
    }

    fun loadHabits() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val habits = getTodayHabitsUseCase()
            _state.value = HabitListState(habits = habits, isLoading = false)
        }
    }

    fun toggleHabitCompletion(habitId: String, isCurrentlyCompleted: Boolean) {
        viewModelScope.launch {
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            try {
                if (isCurrentlyCompleted) {
                    uncompleteHabitUseCase(habitId, today)
                } else {
                    completeHabitUseCase(habitId, today)
                }
                loadHabits()
            } catch (e: Exception) {
                // Ignore for now
            }
        }
    }
}
