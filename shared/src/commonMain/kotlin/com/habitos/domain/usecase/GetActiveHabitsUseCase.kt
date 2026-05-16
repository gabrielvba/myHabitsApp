package com.habitos.domain.usecase

import com.habitos.domain.model.Habit
import com.habitos.domain.repository.HabitRepository

class GetActiveHabitsUseCase(
    private val habitRepository: HabitRepository
) {
    operator fun invoke(): List<Habit> {
        return habitRepository.findAllActive()
    }
}
