package com.habitos.domain.model

sealed class FrequencyType {
    data object Daily : FrequencyType()
    data class Weekly(val timesPerWeek: Int) : FrequencyType()
}
