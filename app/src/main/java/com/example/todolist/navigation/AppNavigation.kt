package com.example.todolist.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.todolist.controller.AlarmController
import com.example.todolist.controller.TaskList
import com.example.todolist.model.Level
import com.example.todolist.model.Wallet
import com.example.todolist.ui.screens.AddTaskScreen
import com.example.todolist.ui.screens.MiniGameScreen
import com.example.todolist.ui.screens.TaskListScreen

object Routes {
    const val TASK_LIST = "task_list"
    const val ADD_TASK = "add_task"
    const val MINI_GAME = "mini_game"
}

@Composable
fun AppNavigation(taskList: TaskList, alarmController: AlarmController, wallet: Wallet, level: Level) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.TASK_LIST) {
        composable(Routes.TASK_LIST) { TaskListScreen(navController, taskList, alarmController, wallet, level) }
        composable(Routes.ADD_TASK) { AddTaskScreen(navController, taskList, alarmController) }
        composable(Routes.MINI_GAME) { MiniGameScreen(navController, level) }
    }
}
