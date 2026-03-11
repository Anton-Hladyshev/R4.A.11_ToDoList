package com.example.todolist.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.todolist.controller.AlarmController
import com.example.todolist.controller.TaskList
import com.example.todolist.model.Level
import com.example.todolist.model.SwordShop
import com.example.todolist.model.Wallet
import com.example.todolist.ui.components.CoinsView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    parentNavController: NavController,
    taskList: TaskList,
    alarmController: AlarmController,
    wallet: Wallet,
    level: Level,
    swordShop: SwordShop
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val title = if (currentDestination?.route == "list") "Ma Liste de Tâches" else "Mini Jeu"
                    Text(title)
                },
                actions = {
                    CoinsView(wallet = wallet, modifier = Modifier.padding(end = 16.dp))
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = null) },
                    label = { Text("Liste") },
                    selected = currentDestination?.hierarchy?.any { it.route == "list" } == true,
                    onClick = {
                        navController.navigate("list") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.PlayArrow, contentDescription = null) },
                    label = { Text("Jeux") },
                    selected = currentDestination?.hierarchy?.any { it.route == "game" } == true,
                    onClick = {
                        navController.navigate("game") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "list",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("list") {
                TaskListScreen(
                    navController = parentNavController,
                    taskList = taskList,
                    alarmController = alarmController,
                    wallet = wallet,
                    level = level,
                    swordShop = swordShop,
                    showScaffold = false
                )
            }
            composable("game") {
                MiniGameScreen(
                    navController = parentNavController,
                    level = level,
                    swordShop = swordShop,
                    wallet = wallet,
                    showScaffold = false
                )
            }
        }
    }
}
