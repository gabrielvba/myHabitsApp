package com.habitos.domain.usecase

import com.habitos.domain.model.CannotCompleteInPastException
import com.habitos.domain.model.HabitAlreadyCompletedException
import com.habitos.domain.model.HabitCompletion
import com.habitos.domain.repository.CompletionRepository
import com.habitos.util.generateUuid
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class CompleteHabitUseCase(
    private val completionRepository: CompletionRepository,
    private val clock: Clock = Clock.System
) {
    operator fun invoke(habitId: String, date: LocalDate): HabitCompletion {
        val now = clock.now()
        val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date

        if (date != today) {
            throw CannotCompleteInPastException()
        }

        val existing = completionRepository.findByHabitIdAndDate(habitId, date)
        if (existing != null) {
            throw HabitAlreadyCompletedException()
        }

        val completion = HabitCompletion(
            id = generateUuid(),
            habitId = habitId,
            date = date,
            completedAt = now
        )

        completionRepository.save(completion)
        return completion
    }
}
