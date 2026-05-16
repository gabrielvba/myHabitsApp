package com.habitos.domain.usecase

import com.habitos.domain.model.HabitWithStatus
import com.habitos.domain.repository.CompletionRepository
import com.habitos.domain.repository.HabitRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class GetTodayHabitsUseCase(
    private val habitRepository: HabitRepository,
    private val completionRepository: CompletionRepository,
    private val getHabitStreakUseCase: GetHabitStreakUseCase,
    private val clock: Clock = Clock.System
) {
    operator fun invoke(): List<HabitWithStatus> {
        val today = clock.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val activeHabits = habitRepository.findAllActive()

        return activeHabits.map { habit ->
            val isCompletedToday = completionRepository.findByHabitIdAndDate(habit.id, today) != null
            val streakInfo = getHabitStreakUseCase(habit.id)

            HabitWithStatus(
                habit = habit,
                isCompletedToday = isCompletedToday,
                currentStreak = streakInfo.current
            )
        }
    }
}
