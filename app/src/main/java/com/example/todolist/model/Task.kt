package com.example.todolist.model

import com.example.todolist.model.enums.State
import com.example.todolist.model.interfaces.Editable
import java.util.Date
import java.util.UUID

data class Task(
    val id: String = UUID.randomUUID().toString(),
    override var title: String,
    override var description: String = "",
    override var endDate: Date = Date(),
    override var endTime: Date = Date(),
    override var state: State = State.TODO,
) : Editable {

    override fun editTitle(newTitle: String) { title = newTitle }
    override fun editDescription(newDescr: String) { description = newDescr }
    override fun changeState(newState: State) { state = newState }
    override fun changeEndTime(newEndTime: Date) { endTime = newEndTime }
    override fun changeEndDate(newEndDate: Date) { endDate = newEndDate }
    fun validate() {
        state = State.DONE
    }
    fun cancel() {
        val now = Date()
        if (isLate()) {
            state = State.LATE
        }
        else {
            state = State.TODO
        }
    }
    fun isLate(): Boolean {
        val now = Date()
        return now > endDate && now > endTime
    }
}