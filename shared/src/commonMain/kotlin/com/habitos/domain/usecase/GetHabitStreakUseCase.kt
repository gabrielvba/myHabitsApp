package com.habitos.domain.usecase

import com.habitos.domain.model.FrequencyType
import com.habitos.domain.model.StreakInfo
import com.habitos.domain.repository.CompletionRepository
import com.habitos.domain.repository.HabitRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class GetHabitStreakUseCase(
    private val habitRepository: HabitRepository,
    private val completionRepository: CompletionRepository,
    private val clock: Clock = Clock.System
) {
    operator fun invoke(habitId: String): StreakInfo {
        val habit = habitRepository.findById(habitId) ?: return StreakInfo(0, 0)
        val completions = completionRepository.findByHabitId(habitId)

        val today = clock.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

        val (current, best) = when (val freq = habit.frequency) {
            is FrequencyType.Daily -> DailyStreakCalculator.calculate(completions, today, habit.createdAt)
            is FrequencyType.Weekly -> WeeklyStreakCalculator.calculate(completions, today, habit.createdAt, freq.timesPerWeek)
        }

        return StreakInfo(current, best)
    }
}
