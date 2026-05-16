package com.habitos.androidApp.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitos.domain.model.StreakInfo
import com.habitos.domain.usecase.GetHabitHistoryUseCase
import com.habitos.domain.usecase.GetHabitStreakUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class HabitDetailState(
    val streak: StreakInfo? = null,
    val historyDates: List<LocalDate> = emptyList(),
    val currentMonth: Int = 1,
    val currentYear: Int = 2026,
    val isLoading: Boolean = true
)

class HabitDetailViewModel(
    private val habitId: String,
    private val getHabitStreakUseCase: GetHabitStreakUseCase,
    private val getHabitHistoryUseCase: GetHabitHistoryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HabitDetailState())
    val state: StateFlow<HabitDetailState> = _state.asStateFlow()

    init {
        loadDetail()
    }

    private fun loadDetail() {
        viewModelScope.launch {
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val streak = getHabitStreakUseCase(habitId)
            val history = getHabitHistoryUseCase(habitId, today.year, today.monthNumber)

            _state.value = _state.value.copy(
                streak = streak,
                historyDates = history,
                currentMonth = today.monthNumber,
                currentYear = today.year,
                isLoading = false
            )
        }
    }
}
