package com.example.todolist.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.todolist.controller.AlarmController
import com.example.todolist.controller.MonsterManager
import com.example.todolist.controller.PhotoController
import com.example.todolist.controller.TaskList
import com.example.todolist.model.Level
import com.example.todolist.model.SwordShop
import com.example.todolist.model.Wallet
import com.example.todolist.ui.screens.*

object Routes {
    const val MAIN = "main"
    const val ADD_TASK = "add_task"
    const val MINI_GAME = "mini_game"
}

@Composable
fun AppNavigation(
    taskList: TaskList,
    alarmController: AlarmController,
    wallet: Wallet,
    level: Level,
    swordShop: SwordShop,
    monsterManager: MonsterManager,
    photoController: PhotoController
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.MAIN) {
        composable(Routes.MAIN) {
            MainScreen(navController, taskList, alarmController, wallet, level, swordShop, monsterManager, photoController)
        }
        composable(Routes.ADD_TASK) {
            AddTaskScreen(navController, taskList, alarmController, photoController)
        }
        composable(Routes.MINI_GAME) {
            MiniGameScreen(navController, level, swordShop, wallet, monsterManager)
        }
    }
}
