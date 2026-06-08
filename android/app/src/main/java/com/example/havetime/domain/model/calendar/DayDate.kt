package com.example.havetime.domain.model.calendar

import com.example.havetime.domain.model.Activity
import java.time.LocalDate

data class DayDate(
    val date: LocalDate,
    val activities: List<Activity> = emptyList()
)
