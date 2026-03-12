package com.example.todolist.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.todolist.R
import com.example.todolist.controller.AlarmController
import com.example.todolist.controller.MonsterManager
import com.example.todolist.controller.PhotoController
import com.example.todolist.controller.TaskList
import com.example.todolist.model.Level
import com.example.todolist.model.SwordShop
import com.example.todolist.model.Wallet
import com.example.todolist.ui.components.CoinsView

sealed class BottomNavItem(val route: String, val icon: ImageVector, val labelResId: Int, val titleResId: Int) {
    object List : BottomNavItem("list", Icons.Default.List, R.string.nav_list, R.string.task_list_title)
    object Game : BottomNavItem("game", Icons.Default.PlayArrow, R.string.nav_game, R.string.mini_game_title)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    parentNavController: NavController,
    taskList: TaskList,
    alarmController: AlarmController,
    wallet: Wallet,
    level: Level,
    swordShop: SwordShop,
    monsterManager: MonsterManager,
    photoController: PhotoController
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    val items = listOf(
        BottomNavItem.List,
        BottomNavItem.Game
    )

    Scaffold(
        topBar = {
            MainTopBar(currentDestination, items, wallet)
        },
        bottomBar = {
            MainBottomNavigation(navController, currentDestination, items)
        }
    ) { innerPadding ->
        MainNavigationHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            parentNavController = parentNavController,
            taskList = taskList,
            alarmController = alarmController,
            wallet = wallet,
            level = level,
            swordShop = swordShop,
            monsterManager = monsterManager,
            photoController = photoController
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainTopBar(
    currentDestination: NavDestination?,
    items: List<BottomNavItem>,
    wallet: Wallet
) {
    val currentItem = items.find { it.route == currentDestination?.route }
    TopAppBar(
        title = { 
            if (currentItem != null) {
                Text(stringResource(id = currentItem.titleResId))
            }
        },
        actions = {
            CoinsView(wallet = wallet, modifier = Modifier.padding(end = 16.dp))
        }
    )
}

@Composable
private fun MainBottomNavigation(
    navController: NavHostController,
    currentDestination: NavDestination?,
    items: List<BottomNavItem>
) {
    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(stringResource(id = item.labelResId)) },
                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                onClick = {
                    navController.navigate(item.route) {
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
}

@Composable
private fun MainNavigationHost(
    navController: NavHostController,
    modifier: Modifier,
    parentNavController: NavController,
    taskList: TaskList,
    alarmController: AlarmController,
    wallet: Wallet,
    level: Level,
    swordShop: SwordShop,
    monsterManager: MonsterManager,
    photoController: PhotoController
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.List.route,
        modifier = modifier
    ) {
        composable(BottomNavItem.List.route) {
            TaskListScreen(
                navController = parentNavController,
                taskList = taskList,
                alarmController = alarmController,
                wallet = wallet,
                level = level,
                swordShop = swordShop,
                showScaffold = false,
                photoController = photoController
            )
        }
        composable(BottomNavItem.Game.route) {
            MiniGameScreen(
                navController = parentNavController,
                level = level,
                swordShop = swordShop,
                wallet = wallet,
                monsterManager = monsterManager,
                showScaffold = false
            )
        }
    }
}
