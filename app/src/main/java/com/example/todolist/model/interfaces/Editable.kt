package com.example.todolist.model.interfaces

import com.example.todolist.model.enums.Periodicity
import com.example.todolist.model.enums.State
import java.util.Date

interface Editable {

    var endDate: Date
    var endTime: Date
    var state: State
    var periodicity: Periodicity?
    var description: String
    var title: String

    fun editTitle(newTitle: String)
    fun editDescription(newDescr: String)
    fun changeState(newState: State)

    fun changePeriodicity(newPeriodicity: Periodicity)
    fun changeEndTime(newEndTime: Date)
    fun changeEndDate(newEndDate: Date)
}