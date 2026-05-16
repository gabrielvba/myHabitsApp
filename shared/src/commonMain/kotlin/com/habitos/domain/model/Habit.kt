package com.habitos.domain.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

data class Habit(
    val id: String,
    val name: String,
    val emoji: String?,
    val color: String,
    val frequency: FrequencyType,
    val createdAt: LocalDate,
    val archivedAt: LocalDate? = null,
    val updatedAt: Instant
)
