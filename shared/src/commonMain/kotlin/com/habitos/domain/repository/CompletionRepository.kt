package com.habitos.domain.repository

import com.habitos.domain.model.HabitCompletion
import kotlinx.datetime.LocalDate

interface CompletionRepository {
    fun save(completion: HabitCompletion)
    fun delete(habitId: String, date: LocalDate)
    fun findByHabitId(habitId: String): List<HabitCompletion>
    fun findByHabitIdAndDate(habitId: String, date: LocalDate): HabitCompletion?
    fun findByHabitIdBetween(habitId: String, startDate: LocalDate, endDate: LocalDate): List<HabitCompletion>
}
