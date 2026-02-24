package com.example.todolist.controller

import androidx.compose.runtime.mutableStateListOf
import com.example.todolist.model.Todo

class TodoController {

    private var nextId = 0
    val todos = mutableStateListOf<Todo>()

    fun addTodo(title: String, description: String = "") {
        if (title.isBlank()) return
        todos.add(Todo(id = nextId++, title = title, description = description))
    }

    fun removeTodo(id: Int) {
        todos.removeAll { it.id == id }
    }

    fun toggleCompleted(id: Int) {
        val index = todos.indexOfFirst { it.id == id }
        if (index != -1) {
            todos[index] = todos[index].copy(isCompleted = !todos[index].isCompleted)
        }
    }
}

