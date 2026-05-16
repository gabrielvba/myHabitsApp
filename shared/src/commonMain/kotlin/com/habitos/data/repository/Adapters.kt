package com.habitos.data.repository

import app.cash.sqldelight.ColumnAdapter
import com.habitos.domain.model.FrequencyType
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

val localDateAdapter = object : ColumnAdapter<LocalDate, String> {
    override fun decode(databaseValue: String): LocalDate {
        return LocalDate.parse(databaseValue)
    }

    override fun encode(value: LocalDate): String {
        return value.toString()
    }
}

val instantAdapter = object : ColumnAdapter<Instant, String> {
    override fun decode(databaseValue: String): Instant {
        return Instant.parse(databaseValue)
    }

    override fun encode(value: Instant): String {
        return value.toString()
    }
}
