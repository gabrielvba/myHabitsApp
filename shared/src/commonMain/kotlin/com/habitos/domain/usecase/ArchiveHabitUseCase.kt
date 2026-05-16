package com.habitos.domain.usecase

import com.habitos.domain.repository.HabitRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class ArchiveHabitUseCase(
    private val habitRepository: HabitRepository,
    private val clock: Clock = Clock.System
) {
    operator fun invoke(id: String) {
        val habit = habitRepository.findById(id)
            ?: throw IllegalArgumentException("Hábito não encontrado")

        val now = clock.now()
        val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date

        val archivedHabit = habit.copy(
            archivedAt = today,
            updatedAt = now
        )

        habitRepository.save(archivedHabit)
    }
}
