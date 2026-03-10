package com.example.todolist.controller

import com.example.todolist.model.Task // Note: Standardize your Task import
import com.example.todolist.model.interfaces.TaskSpecification

class TaskList {
    private val _tasks = mutableListOf<Task>()

    val tasks: List<Task> get() = _tasks

    fun addTask(task: Task) {
        _tasks.add(task)
    }

    fun getFilteredTasks(filter: TaskSpecification): List<Task> {
        return _tasks.filter { filter.isSatisfiedBy(it) }
    }
}