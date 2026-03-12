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
import com.example.todolist.controller.PhotoController
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
    private lateinit var monsterManager: MonsterManager
    private lateinit var photoController: PhotoController

    private val wallet = Wallet()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        val msgResId = if (isGranted) R.string.notifications_granted else R.string.notifications_denied
        Toast.makeText(this, getString(msgResId), Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        alarmController = AlarmController(this)
        level = Level(this)
        swordShop = SwordShop(this)
        monsterManager = MonsterManager(this)
        photoController = PhotoController(this)
        checkNotificationPermission()
        enableEdgeToEdge()
        setContent {
            ToDoListTheme {
                AppNavigation(taskList, alarmController, wallet, level, swordShop, monsterManager, photoController)
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
