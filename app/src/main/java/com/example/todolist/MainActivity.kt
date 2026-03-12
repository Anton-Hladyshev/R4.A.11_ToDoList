package com.example.todolist

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.todolist.controller.AlarmController
import com.example.todolist.controller.MonsterManager
import com.example.todolist.controller.TaskList
import com.example.todolist.model.Level
import com.example.todolist.model.SwordShop
import com.example.todolist.model.Wallet
import com.example.todolist.navigation.AppNavigation
import com.example.todolist.ui.theme.ToDoListTheme
import com.example.todolist.utils.CsvManager

class MainActivity : ComponentActivity() {

    private lateinit var taskList: TaskList
    private lateinit var alarmController: AlarmController
    private lateinit var level: Level
    private lateinit var swordShop: SwordShop
    private lateinit var monsterManager: MonsterManager
    private lateinit var wallet: Wallet

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        val msgResId = if (isGranted) R.string.notifications_granted else R.string.notifications_denied
        Toast.makeText(this, getString(msgResId), Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 1. Load general game data (balance, level, xp, sword)
        val gameData = CsvManager.loadGameData(this)
        
        // 2. Initialize models with saved data
        val initialBalance = gameData["balance"] ?: 0
        wallet = Wallet(initialBalance)
        
        level = Level(this)
        level.restoreProgress(
            level = gameData["level"] ?: 1,
            xp = gameData["xp"] ?: 0
        )
        
        swordShop = SwordShop(this)
        swordShop.restoreProgress(
            swordIndex = gameData["swordIndex"] ?: 0
        )

        // 3. Load tasks
        taskList = TaskList()
        val savedTasks = CsvManager.loadTasks(this)
        savedTasks.forEach { taskList.addTask(it) }
        
        // 4. Other controllers
        alarmController = AlarmController(this)
        monsterManager = MonsterManager(this)
        
        checkNotificationPermission()
        enableEdgeToEdge()
        
        setContent {
            ToDoListTheme {
                AppNavigation(taskList, alarmController, wallet, level, swordShop, monsterManager)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        // Save everything when app is closed or put in background
        CsvManager.saveTasks(this, taskList.tasks)
        CsvManager.saveGameData(
            context = this,
            balance = wallet.balance,
            level = level.currentLevel,
            xp = level.currentXp,
            swordIndex = swordShop.currentSwordIndex
        )
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
