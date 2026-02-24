package com.example.todolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.todolist.controller.TodoController
import com.example.todolist.ui.theme.ToDoListTheme
import com.example.todolist.view.TodoListScreen

class MainActivity : ComponentActivity() {
    private val todoController = TodoController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToDoListTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TodoListScreen(
                        controller = todoController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
