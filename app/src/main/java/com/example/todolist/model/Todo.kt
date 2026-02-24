package com.example.todolist.model

data class Todo(
    val id: Int = 0,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false
)

