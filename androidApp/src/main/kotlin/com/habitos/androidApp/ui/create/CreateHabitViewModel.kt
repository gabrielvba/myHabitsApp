package com.habitos.androidApp.ui.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitos.domain.model.FrequencyType
import com.habitos.domain.usecase.CreateHabitUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CreateHabitState(
    val name: String = "",
    val emoji: String = "📝",
    val color: String = "#4CAF50",
    val isWeekly: Boolean = false,
    val timesPerWeek: Int = 1,
    val errorMessage: String? = null,
    val isSaved: Boolean = false
)

class CreateHabitViewModel(
    private val createHabitUseCase: CreateHabitUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(CreateHabitState())
    val state: StateFlow<CreateHabitState> = _state.asStateFlow()

    fun updateName(name: String) { _state.value = _state.value.copy(name = name, errorMessage = null) }
    fun updateEmoji(emoji: String) { _state.value = _state.value.copy(emoji = emoji) }
    fun updateFrequency(isWeekly: Boolean) { _state.value = _state.value.copy(isWeekly = isWeekly) }
    fun updateTimesPerWeek(times: Int) { _state.value = _state.value.copy(timesPerWeek = times) }

    fun save() {
        val currentState = _state.value
        if (currentState.name.isBlank()) {
            _state.value = currentState.copy(errorMessage = "Nome do hábito não pode ser vazio")
            return
        }

        val frequency = if (currentState.isWeekly) {
            FrequencyType.Weekly(currentState.timesPerWeek)
        } else {
            FrequencyType.Daily
        }

        viewModelScope.launch {
            try {
                createHabitUseCase(
                    name = currentState.name,
                    emoji = currentState.emoji,
                    color = currentState.color,
                    frequency = frequency
                )
                _state.value = _state.value.copy(isSaved = true)
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = e.message)
            }
        }
    }
}
