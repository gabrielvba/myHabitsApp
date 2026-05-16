package com.habitos.domain.repository

import com.habitos.domain.model.HabitCompletion
import kotlinx.datetime.LocalDate

class FakeCompletionRepository : CompletionRepository {
    private val completions = mutableListOf<HabitCompletion>()

    override fun save(completion: HabitCompletion) {
        completions.add(completion)
    }

    override fun delete(habitId: String, date: LocalDate) {
        completions.removeAll { it.habitId == habitId && it.date == date }
    }

    override fun findByHabitId(habitId: String): List<HabitCompletion> {
        return completions.filter { it.habitId == habitId }
    }

    override fun findByHabitIdAndDate(habitId: String, date: LocalDate): HabitCompletion? {
        return completions.find { it.habitId == habitId && it.date == date }
    }

    override fun findByHabitIdBetween(habitId: String, startDate: LocalDate, endDate: LocalDate): List<HabitCompletion> {
        return completions.filter {
            it.habitId == habitId &&
            it.date >= startDate &&
            it.date <= endDate
        }
    }

    fun clear() {
        completions.clear()
    }
}
