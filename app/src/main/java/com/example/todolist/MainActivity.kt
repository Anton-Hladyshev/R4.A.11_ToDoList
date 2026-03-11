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
import com.example.todolist.controller.TaskList
import com.example.todolist.model.Level
import com.example.todolist.model.SwordShop
import com.example.todolist.model.Wallet
import com.example.todolist.navigation.AppNavigation
import com.example.todolist.ui.theme.ToDoListTheme

class MainActivity : ComponentActivity() {

    private val taskList = TaskList()
    private lateinit var alarmController: AlarmController
    private lateinit var level: Level
    private lateinit var swordShop: SwordShop

    private val wallet = Wallet()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Notifications autorisées", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Notifications refusées", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        alarmController = AlarmController(this)
        level = Level(this)
        swordShop = SwordShop(this)
        wallet.deposit(10000)
        checkNotificationPermission()
        enableEdgeToEdge()
        setContent {
            ToDoListTheme {
                AppNavigation(taskList, alarmController, wallet, level, swordShop)
            }
        }
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
