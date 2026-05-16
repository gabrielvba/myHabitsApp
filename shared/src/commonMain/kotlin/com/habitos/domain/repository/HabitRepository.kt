package com.habitos.domain.repository

import com.habitos.domain.model.Habit

interface HabitRepository {
    fun save(habit: Habit)
    fun findById(id: String): Habit?
    fun findAllActive(): List<Habit>
    fun archive(id: String)
}
