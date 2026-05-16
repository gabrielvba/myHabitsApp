package com.habitos.domain.usecase

import com.habitos.domain.model.CompletionNotFoundException
import com.habitos.domain.repository.CompletionRepository
import kotlinx.datetime.LocalDate

class UncompleteHabitUseCase(
    private val completionRepository: CompletionRepository
) {
    operator fun invoke(habitId: String, date: LocalDate) {
        val existing = completionRepository.findByHabitIdAndDate(habitId, date)
            ?: throw CompletionNotFoundException()

        completionRepository.delete(habitId, existing.date)
    }
}
