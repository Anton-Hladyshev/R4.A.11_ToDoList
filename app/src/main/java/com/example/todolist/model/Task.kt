package com.example.todolist.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.todolist.model.enums.Periodicity
import com.example.todolist.model.enums.Priority
import com.example.todolist.model.enums.State
import com.example.todolist.model.interfaces.Editable
import kotlinx.coroutines.delay
import java.util.Calendar
import java.util.UUID

class Task(
    val id: String = UUID.randomUUID().toString(),
    title: String,
    description: String = "",
    deadline: Calendar = Calendar.getInstance(),
    state: State = State.TODO,
    periodicity: Periodicity? = null,
    priority: Priority? = null
) : Editable {

    override var title by mutableStateOf(title)
    override var description by mutableStateOf(description)
    override var deadline by mutableStateOf(deadline)
    override var state by mutableStateOf(state)
    override var periodicity by mutableStateOf(periodicity)
    override var priority by mutableStateOf(priority)

    override fun editTitle(newTitle: String) { title = newTitle }
    override fun editDescription(newDescr: String) { description = newDescr }
    override fun changeState(newState: State) { state = newState }
    
    override fun updateDeadlineDate(newDateMillis: Long) {
        val newDate = Calendar.getInstance().apply { timeInMillis = newDateMillis }
        val updatedDeadline = (deadline.clone() as Calendar).apply {
            set(Calendar.YEAR, newDate.get(Calendar.YEAR))
            set(Calendar.MONTH, newDate.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, newDate.get(Calendar.DAY_OF_MONTH))
        }
        deadline = updatedDeadline
    }

    override fun updateDeadlineTime(hour: Int, minute: Int) {
        val updatedDeadline = (deadline.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        deadline = updatedDeadline
    }

    override fun changePeriodicity(newPeriodicity: Periodicity?) {
        periodicity = newPeriodicity
    }

    override fun changePriority(newPriority: Priority?) {
        priority = newPriority
    }

    suspend fun validate() {
        if (periodicity == null) {
            state = State.DONE
        } else {
            state = State.DONE
            delay(2000)
            val updatedDeadline = (deadline.clone() as Calendar).apply {
                add(Calendar.DAY_OF_YEAR, periodicity!!.period.days)
                add(Calendar.MONTH, periodicity!!.period.months)
                add(Calendar.YEAR, periodicity!!.period.years)
            }
            deadline = updatedDeadline
            state = State.TODO
        }
    }
    
    fun cancel() {
        state = if (isLate()) State.LATE else State.TODO
    }
    
    fun isLate(): Boolean {
        return Calendar.getInstance().after(deadline)
    }
}
