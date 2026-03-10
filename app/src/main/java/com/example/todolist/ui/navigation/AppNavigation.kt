package com.example.todolist.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.todolist.controller.TaskList
import com.example.todolist.ui.screens.AddTaskScreen
import com.example.todolist.ui.screens.TaskListScreen

@Composable
fun AppNavigation(taskList: TaskList) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "task_list") {
        composable("task_list") { TaskListScreen(navController, taskList) }
        composable("add_task") { AddTaskScreen(navController, taskList) }
    }
}

