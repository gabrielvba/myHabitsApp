package com.habitos.domain.usecase

import com.habitos.domain.repository.CompletionRepository
import kotlinx.datetime.LocalDate

class GetHabitHistoryUseCase(
    private val completionRepository: CompletionRepository
) {
    operator fun invoke(habitId: String, year: Int, month: Int): List<LocalDate> {
        val startDate = LocalDate(year, month, 1)

        // Find last day of month
        val nextMonth = if (month == 12) 1 else month + 1
        val nextMonthYear = if (month == 12) year + 1 else year
        val firstOfNextMonth = LocalDate(nextMonthYear, nextMonth, 1)
        val endDate = LocalDate.fromEpochDays(firstOfNextMonth.toEpochDays() - 1)

        val completions = completionRepository.findByHabitIdBetween(habitId, startDate, endDate)
        return completions.map { it.date }
    }
}
