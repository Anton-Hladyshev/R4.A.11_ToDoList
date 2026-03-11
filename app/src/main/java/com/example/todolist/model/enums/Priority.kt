package com.example.todolist.model.enums

enum class Priority(var level: Int, var title: String) {
    CRITICAL(3, "Critique"),
    IMPORTANT(2, "Importante"),
    ROUTINE(1, "Routine"),
    SOMEDAY(0, "Un jour")
}