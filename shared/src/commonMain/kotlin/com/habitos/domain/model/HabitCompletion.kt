package com.habitos.domain.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

data class HabitCompletion(
    val id: String,
    val habitId: String,
    val date: LocalDate,
    val completedAt: Instant
)
