package com.habitos.domain.usecase

import com.habitos.domain.model.FrequencyType
import com.habitos.domain.model.Habit
import com.habitos.domain.repository.HabitRepository
import com.habitos.util.generateUuid
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class CreateHabitUseCase(
    private val habitRepository: HabitRepository,
    private val clock: Clock = Clock.System
) {
    operator fun invoke(
        name: String,
        emoji: String?,
        color: String,
        frequency: FrequencyType
    ): Habit {
        if (name.isBlank()) {
            throw IllegalArgumentException("O nome do hábito não pode ser vazio")
        }

        val now = clock.now()
        val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date

        val habit = Habit(
            id = generateUuid(),
            name = name.trim(),
            emoji = emoji,
            color = color,
            frequency = frequency,
            createdAt = today,
            archivedAt = null,
            updatedAt = now
        )

        habitRepository.save(habit)
        return habit
    }
}
