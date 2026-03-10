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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.todolist.controller.AlarmController
import com.example.todolist.controller.TaskList
import com.example.todolist.model.Task
import com.example.todolist.model.enums.State
import com.example.todolist.ui.theme.ToDoListTheme
import java.util.Calendar

class MainActivity : ComponentActivity() {

    private val taskList = TaskList()
    private lateinit var alarmController: AlarmController

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
        
        checkNotificationPermission()
        
        enableEdgeToEdge()
        setContent {
            ToDoListTheme {
                AppNavigation(taskList, alarmController)
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

@Composable
fun TaskItem(modifier: Modifier = Modifier, task: com.example.todolist.model.Task, alarmController: AlarmController) {
    var isDone by remember { mutableStateOf(task.state == com.example.todolist.model.enums.State.DONE) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val (stateColor, stateLabel) = when (task.state) {
                com.example.todolist.model.enums.State.DONE -> Color(0xFF4CAF50) to "DONE"
                com.example.todolist.model.enums.State.TODO -> Color(0xFFFF9800) to "TODO"
                else -> Color(0xFFFF5252) to "LATE"
            }

            Checkbox(
                checked = isDone,
                onCheckedChange = { checked ->
                    isDone = checked
                    if (checked) {
                        task.validate()
                        alarmController.cancelAlarms(task)
                    } else {
                        task.cancel()
                        alarmController.scheduleTaskAlarm(task)
                    }
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF4CAF50),
                    uncheckedColor = if (task.state == State.TODO ) Color(0xFFFF9800) else Color(0xFFFF5252)
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isDone) Color.Gray else Color.Black
                )
                if (task.description.isNotEmpty()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Text(
                text = stateLabel,
                style = MaterialTheme.typography.labelMedium,
                color = stateColor,
                modifier = Modifier
                    .padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun AppNavigation(taskList: TaskList, alarmController: AlarmController) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "task_list") {
        composable("task_list") { TaskListScreen(navController, taskList, alarmController) }
        composable("add_task") { AddTaskScreen(navController, taskList, alarmController) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(navController: NavController, taskList: TaskList, alarmController: AlarmController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Ma Liste de Tâches") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0xFFF1F1F1), shape = RoundedCornerShape(16.dp))
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (taskList.tasks.isNotEmpty()) {
                    for (task in taskList.tasks) {
                        TaskItem(task = task, alarmController = alarmController)
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Aucune tâche pour le moment...",
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("add_task") },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Créer une nouvelle tâche")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(navController: NavController, taskList: TaskList, alarmController: AlarmController) {
    var taskTitle by remember { mutableStateOf("") }
    var taskDescription by remember { mutableStateOf("") }

    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }
    val selectedDate = datePickerState.selectedDateMillis?.let {
        java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date(it))
    } ?: "Choisir une date"

    val timePickerState = rememberTimePickerState()
    var showTimePicker by remember { mutableStateOf(false) }
    val selectedTime = "${timePickerState.hour.toString().padStart(2, '0')}:${timePickerState.minute.toString().padStart(2, '0')}"

    var expanded by remember { mutableStateOf(false) }
    val periodiciteOptions = listOf("Aucune", "Quotidien", "Hebdomadaire", "Mensuel")
    var selectedPeriodicite by remember { mutableStateOf(periodiciteOptions[0]) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Nouvelle Tâche") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = taskTitle,
                onValueChange = { taskTitle = it },
                label = { Text("Titre") },
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = taskDescription,
                onValueChange = { taskDescription = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                maxLines = 5
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = selectedDate)
                }

                OutlinedButton(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = selectedTime)
                }
            }

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = { showDatePicker = false }) { Text("OK") }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            if (showTimePicker) {
                TimePickerDialog(
                    onDismissRequest = { showTimePicker = false },
                    confirmButton = {
                        TextButton(onClick = { showTimePicker = false }) { Text("OK") }
                    }
                ) {
                    TimePicker(state = timePickerState)
                }
            }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = selectedPeriodicite,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Périodicité") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    periodiciteOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedPeriodicite = option
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Retour")
                }
                Button(
                    onClick = {
                        val deadline = Calendar.getInstance().apply {
                            timeInMillis = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                            set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                            set(Calendar.MINUTE, timePickerState.minute)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }

                        val newTask = Task(
                            title = taskTitle,
                            description = taskDescription,
                            deadline = deadline,
                            state = com.example.todolist.model.enums.State.TODO
                        )
                        taskList.addTask(newTask)
                        alarmController.scheduleTaskAlarm(newTask)
                        navController.popBackStack()
                    },
                    modifier = Modifier.weight(1f),
                    enabled = taskTitle.isNotBlank()
                ) {
                    Text("Valider")
                }
            }
        }
    }
}

@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        text = { content() }
    )
}
