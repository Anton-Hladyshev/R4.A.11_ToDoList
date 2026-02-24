package com.example.todolist.controller

import com.example.todolist.model.Task // Note: Standardize your Task import

class TaskList {
    private val _tasks = mutableListOf<Task>()

    val tasks: List<Task> get() = _tasks

    fun addTask(task: Task) {
        _tasks.add(task)
    }
}