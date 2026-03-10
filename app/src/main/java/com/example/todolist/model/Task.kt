package com.example.todolist.model

import com.example.todolist.model.enums.Periodicity
import com.example.todolist.model.enums.State
import com.example.todolist.model.interfaces.Editable
import java.util.Calendar
import java.util.UUID

data class Task(
    val id: String = UUID.randomUUID().toString(),
    override var title: String,
    override var description: String = "",
    override var deadline: Calendar = Calendar.getInstance(),
    override var state: State = State.TODO,
    override var periodicity: Periodicity? = null
) : Editable {

    override fun editTitle(newTitle: String) { title = newTitle }
    override fun editDescription(newDescr: String) { description = newDescr }
    override fun changeState(newState: State) { state = newState }
    
    override fun updateDeadlineDate(newDateMillis: Long) {
        val newDate = Calendar.getInstance().apply { timeInMillis = newDateMillis }
        deadline.set(Calendar.YEAR, newDate.get(Calendar.YEAR))
        deadline.set(Calendar.MONTH, newDate.get(Calendar.MONTH))
        deadline.set(Calendar.DAY_OF_MONTH, newDate.get(Calendar.DAY_OF_MONTH))
    }

    override fun updateDeadlineTime(hour: Int, minute: Int) {
        deadline.set(Calendar.HOUR_OF_DAY, hour)
        deadline.set(Calendar.MINUTE, minute)
        deadline.set(Calendar.SECOND, 0)
        deadline.set(Calendar.MILLISECOND, 0)
    }

    override fun changePeriodicity(newPeriodicity: Periodicity?) {
        periodicity = newPeriodicity
    }

    fun validate() {
        state = State.DONE
    }
    
    fun cancel() {
        state = if (isLate()) State.LATE else State.TODO
    }
    
    fun isLate(): Boolean {
        return Calendar.getInstance().after(deadline)
    }
}