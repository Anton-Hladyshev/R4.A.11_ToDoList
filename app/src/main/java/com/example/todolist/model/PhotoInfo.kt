package com.example.todolist.model

import java.util.UUID

data class PhotoInfo(
    val id: String = UUID.randomUUID().toString(),
    val taskId: String,
    val fileName: String,
    val timestamp: Long = System.currentTimeMillis()
)
