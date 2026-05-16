package com.habitos.util

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus

fun LocalDate.startOfWeekIso(): LocalDate {
    // dayOfWeek is an enum where MONDAY is 1 and SUNDAY is 7
    return this.minus(this.dayOfWeek.value - 1, DateTimeUnit.DAY)
}
