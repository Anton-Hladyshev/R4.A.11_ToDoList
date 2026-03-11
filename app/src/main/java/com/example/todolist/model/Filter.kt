package com.example.todolist.model
import com.example.todolist.model.enums.State
import com.example.todolist.model.enums.Periodicity
import com.example.todolist.model.enums.Priority
import com.example.todolist.model.interfaces.TaskSpecification
import java.util.Date

data class Filter(
    var stateFilter: State? = null,
    var periodicityFilter: Periodicity? = null,
    var priorityFilter: Priority? = null,
    var endDateFilter: Date? = null,
    var endTimeFilter: Date? = null
) : TaskSpecification {
    override fun isSatisfiedBy(task: Task) : Boolean {
        return (stateFilter == null || task.state == stateFilter) &&
                (periodicityFilter == null || task.periodicity == periodicityFilter) &&
                (priorityFilter == null || task.priority == priorityFilter) &&
                (endDateFilter == null || !task.deadline.time.after(endDateFilter)) &&
                (endTimeFilter == null || !task.deadline.time.after(endTimeFilter))
    }
}
