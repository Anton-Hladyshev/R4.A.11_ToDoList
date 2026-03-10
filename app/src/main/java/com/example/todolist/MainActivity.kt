package com.example.todolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.todolist.controller.TaskList
import com.example.todolist.model.Task
import com.example.todolist.model.enums.State
import com.example.todolist.ui.navigation.AppNavigation
import com.example.todolist.ui.theme.ToDoListTheme

class MainActivity : ComponentActivity() {

    val taskList = TaskList()
    val taskTest = Task(title =  "Tache 1", state = State.TODO)
    val taskTest1 = Task(title =  "Tache 2", state = State.DONE)
    val taskTest2 = Task(title =  "Tache 3", state = State.LATE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskList.addTask(taskTest)
        taskList.addTask(taskTest1)
        taskList.addTask(taskTest2)
        enableEdgeToEdge()
        setContent {
            ToDoListTheme {
                AppNavigation(taskList)
            }
        }
    }
}
