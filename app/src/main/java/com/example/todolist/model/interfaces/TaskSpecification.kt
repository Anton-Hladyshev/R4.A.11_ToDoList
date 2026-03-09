package com.example.todolist.model.interfaces

import com.example.todolist.model.Task

interface TaskSpecification {
    fun isSatisfiedBy(task: Task): Boolean
}