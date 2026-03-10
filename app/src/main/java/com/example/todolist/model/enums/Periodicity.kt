package com.example.todolist.model.enums

import java.time.Period

enum class Periodicity(val period: Period) {
    DAILY(Period.ofDays(1)),
    WEEKLY(Period.ofWeeks(1)),
    MONTHLY(Period.ofMonths(1))
}