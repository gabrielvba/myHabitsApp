package com.habitos.domain.usecase

import com.habitos.domain.model.HabitCompletion
import com.habitos.util.startOfWeekIso
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus

object DailyStreakCalculator {
    fun calculate(completions: List<HabitCompletion>, today: LocalDate, createdAt: LocalDate): Pair<Int, Int> {
        if (completions.isEmpty()) return 0 to 0

        val sortedDates = completions.map { it.date }.sortedDescending()

        var currentStreak = 0
        var bestStreak = 0
        var tempStreak = 0

        // Loop on all consecutive days from createdAt to today
        var currentCalculationDate = today
        var firstIteration = true
        var brokeCurrent = false

        while (currentCalculationDate >= createdAt) {
            val hasCompletion = sortedDates.contains(currentCalculationDate)

            if (hasCompletion) {
                tempStreak++
            } else {
                if (firstIteration) {
                    // if it is today and missed, the current streak doesn't break entirely if yesterday was completed
                    // however, the temp streak is broken.
                } else {
                    brokeCurrent = true
                }

                if (tempStreak > bestStreak) {
                    bestStreak = tempStreak
                }

                tempStreak = 0
            }

            if (!brokeCurrent && hasCompletion) {
                currentStreak++
            } else if (!brokeCurrent && !hasCompletion && !firstIteration) {
                brokeCurrent = true
            }

            currentCalculationDate = currentCalculationDate.minus(1, DateTimeUnit.DAY)
            firstIteration = false
        }

        if (tempStreak > bestStreak) {
            bestStreak = tempStreak
        }

        return currentStreak to bestStreak
    }
}

object WeeklyStreakCalculator {
    fun calculate(
        completions: List<HabitCompletion>,
        today: LocalDate,
        createdAt: LocalDate,
        timesPerWeek: Int
    ): Pair<Int, Int> {
        if (completions.isEmpty()) return 0 to 0

        val currentWeekStart = today.startOfWeekIso()
        val createdWeekStart = createdAt.startOfWeekIso()

        val weeklyCounts = mutableMapOf<LocalDate, Int>()
        completions.forEach {
            val weekStart = it.date.startOfWeekIso()
            weeklyCounts[weekStart] = (weeklyCounts[weekStart] ?: 0) + 1
        }

        var currentStreak = 0
        var bestStreak = 0
        var tempStreak = 0
        var brokeCurrent = false
        var firstIteration = true

        var checkWeekStart = currentWeekStart

        while (checkWeekStart >= createdWeekStart) {
            val completionsInWeek = weeklyCounts[checkWeekStart] ?: 0
            val metTarget = completionsInWeek >= timesPerWeek

            if (metTarget) {
                tempStreak++
            } else {
                if (firstIteration) {
                    // it's current week, user still has days to complete
                } else {
                    brokeCurrent = true
                }

                if (tempStreak > bestStreak) {
                    bestStreak = tempStreak
                }
                tempStreak = 0
            }

            if (!brokeCurrent && metTarget) {
                currentStreak++
            } else if (!brokeCurrent && !metTarget && !firstIteration) {
                brokeCurrent = true
            }

            checkWeekStart = checkWeekStart.minus(7, DateTimeUnit.DAY)
            firstIteration = false
        }

        if (tempStreak > bestStreak) {
            bestStreak = tempStreak
        }

        return currentStreak to bestStreak
    }
}
