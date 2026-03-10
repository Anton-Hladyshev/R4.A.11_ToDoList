package com.example.todolist

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.todolist.controller.AlarmController
import com.example.todolist.controller.TaskList
import com.example.todolist.model.Filter
import com.example.todolist.model.Task
import com.example.todolist.model.enums.State
import com.example.todolist.ui.theme.ToDoListTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Size
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

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
fun Task(modifier: Modifier = Modifier, task: Task, onTaskCompleted: () -> Unit = {}, onEditClick: () -> Unit = {}, onDeleteClick: () -> Unit = {}, alarmController: AlarmController) {
    var isDone by remember { mutableStateOf(task.state == com.example.todolist.model.enums.State.DONE) }

    val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
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
                State.DONE -> Color(0xFF4CAF50) to "DONE"
                State.TODO -> Color(0xFFFF9800) to "TODO"
                else -> Color(0xFFFF5252) to "LATE"
            }

            Checkbox(
                checked = isDone,
                onCheckedChange = { checked ->
                    isDone = checked
                    if (checked) {
                        task.validate()
                        onTaskCompleted()
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
                // Affichage de la date et de l'heure de fin de la tâche
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Deadline",
                        modifier = Modifier.size(14.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = dateTimeFormat.format(task.deadline.time),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            if (isDone) {
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Supprimer",
                        tint = Color(0xFFFF5252)
                    )
                }
            } else {
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Modifier",
                        tint = MaterialTheme.colorScheme.primary
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
    var showFilters by remember { mutableStateOf(false) }

    // Konfetti state
    var konfettiParties by remember { mutableStateOf<List<Party>>(emptyList()) }

    // Filter state
    var selectedStateFilter by remember { mutableStateOf<State?>(null) }
    var selectedDateFilter by remember { mutableStateOf<Date?>(null) }
    var selectedTimeFilter by remember { mutableStateOf<Date?>(null) }

    // Edit task state
    var taskToEdit by remember { mutableStateOf<com.example.todolist.model.Task?>(null) }
    var refreshCounter by remember { mutableIntStateOf(0) }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val context = LocalContext.current

    // Build filter and get filtered tasks
    val filter = Filter(
        stateFilter = selectedStateFilter,
        endDateFilter = selectedDateFilter,
        endTimeFilter = selectedTimeFilter
    )
    @Suppress("UNUSED_EXPRESSION")
    refreshCounter // read to trigger recomposition
    val filteredTasks = if (selectedStateFilter == null && selectedDateFilter == null && selectedTimeFilter == null) {
        taskList.tasks
    } else {
        taskList.getFilteredTasks(filter)
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
            // Toggle filter button
            OutlinedButton(
                onClick = { showFilters = !showFilters },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (showFilters) "Masquer les filtres" else "Afficher les filtres")
            }

            // Filter section
            AnimatedVisibility(visible = showFilters) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // State filter chips
                    Text(
                        text = "État",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedStateFilter == null,
                            onClick = { selectedStateFilter = null },
                            label = { Text("Tous") }
                        )
                        State.entries.forEach { state ->
                            FilterChip(
                                selected = selectedStateFilter == state,
                                onClick = {
                                    selectedStateFilter = if (selectedStateFilter == state) null else state
                                },
                                label = { Text(state.name) }
                            )
                        }
                    }

                    // Date filter
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedCard(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    val cal = Calendar.getInstance()
                                    if (selectedDateFilter != null) cal.time = selectedDateFilter!!
                                    DatePickerDialog(
                                        context,
                                        { _, year, month, dayOfMonth ->
                                            val newCal = Calendar.getInstance()
                                            newCal.set(year, month, dayOfMonth, 23, 59, 59)
                                            selectedDateFilter = newCal.time
                                        },
                                        cal.get(Calendar.YEAR),
                                        cal.get(Calendar.MONTH),
                                        cal.get(Calendar.DAY_OF_MONTH)
                                    ).show()
                                },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Date",
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (selectedDateFilter != null)
                                        "Avant le ${dateFormat.format(selectedDateFilter!!)}"
                                    else
                                        "Filtrer par date",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        if (selectedDateFilter != null) {
                            IconButton(onClick = { selectedDateFilter = null }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Effacer filtre date"
                                )
                            }
                        }
                    }

                    // Time filter
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedCard(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    val cal = Calendar.getInstance()
                                    if (selectedTimeFilter != null) cal.time = selectedTimeFilter!!
                                    TimePickerDialog(
                                        context,
                                        { _, hourOfDay, minute ->
                                            val newCal = Calendar.getInstance()
                                            newCal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                            newCal.set(Calendar.MINUTE, minute)
                                            selectedTimeFilter = newCal.time
                                        },
                                        cal.get(Calendar.HOUR_OF_DAY),
                                        cal.get(Calendar.MINUTE),
                                        true
                                    ).show()
                                },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Heure",
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (selectedTimeFilter != null)
                                        "Avant ${timeFormat.format(selectedTimeFilter!!)}"
                                    else
                                        "Filtrer par heure",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        if (selectedTimeFilter != null) {
                            IconButton(onClick = { selectedTimeFilter = null }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Effacer filtre heure"
                                )
                            }
                        }
                    }

                    // Reset all filters button
                    if (selectedStateFilter != null || selectedDateFilter != null || selectedTimeFilter != null) {
                        TextButton(
                            onClick = {
                                selectedStateFilter = null
                                selectedDateFilter = null
                                selectedTimeFilter = null
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Réinitialiser tous les filtres", color = Color(0xFFFF5252))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0xFFF1F1F1), shape = RoundedCornerShape(16.dp))
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (filteredTasks.isNotEmpty()) {
                    for (task in filteredTasks) {
                        Task(task = task, onTaskCompleted = {
                            konfettiParties = listOf(
                                Party(
                                    speed = 0f,
                                    maxSpeed = 30f,
                                    damping = 0.9f,
                                    spread = 360,
                                    colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def, 0x4CAF50, 0x2196F3, 0xFF9800),
                                    emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
                                    position = Position.Relative(0.5, 0.3)
                                ),
                                Party(
                                    speed = 10f,
                                    maxSpeed = 50f,
                                    damping = 0.9f,
                                    angle = 270,
                                    spread = 90,
                                    size = listOf(Size.SMALL, Size.LARGE),
                                    colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def, 0x4CAF50, 0x2196F3, 0xFF9800),
                                    emitter = Emitter(duration = 2, TimeUnit.SECONDS).perSecond(50),
                                    position = Position.Relative(0.0, 0.0).between(Position.Relative(1.0, 0.0))
                                )
                            )
                        }, alarmController = alarmController)
                    }
                } else {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (taskList.tasks.isEmpty())
                                "Aucune tâche pour le moment..."
                            else
                                "Aucune tâche ne correspond aux filtres.",
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

        // Konfetti overlay
        if (konfettiParties.isNotEmpty()) {
            KonfettiView(
                modifier = Modifier.fillMaxSize(),
                parties = konfettiParties
            )
            // Reset after a delay so the animation can replay on next check
            LaunchedEffect(konfettiParties) {
                kotlinx.coroutines.delay(3000)
                konfettiParties = emptyList()
            }
        }
    }

    // Edit task dialog
    taskToEdit?.let { task ->
        EditTaskDialog(
            task = task,
            onDismiss = { taskToEdit = null },
            onSave = {
                refreshCounter++
                taskToEdit = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskDialog(
    task: com.example.todolist.model.Task,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    var editedTitle by remember { mutableStateOf(task.title) }
    var editedDescription by remember { mutableStateOf(task.description) }
    var editedDate by remember { mutableStateOf(task.endDate) }
    var editedTime by remember { mutableStateOf(task.endTime) }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val context = LocalContext.current


    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Modifier la tâche") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Title
                OutlinedTextField(
                    value = editedTitle,
                    onValueChange = { editedTitle = it },
                    label = { Text("Titre") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Description
                OutlinedTextField(
                    value = editedDescription,
                    onValueChange = { editedDescription = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )


                // Date picker
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val cal = Calendar.getInstance().apply { time = editedDate }
                            DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    val newCal = Calendar.getInstance()
                                    newCal.set(year, month, dayOfMonth)
                                    editedDate = newCal.time
                                },
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Date",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Date de fin",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.Gray
                            )
                            Text(
                                text = dateFormat.format(editedDate),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                // Time picker
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val cal = Calendar.getInstance().apply { time = editedTime }
                            TimePickerDialog(
                                context,
                                { _, hourOfDay, minute ->
                                    val newCal = Calendar.getInstance()
                                    newCal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                    newCal.set(Calendar.MINUTE, minute)
                                    editedTime = newCal.time
                                },
                                cal.get(Calendar.HOUR_OF_DAY),
                                cal.get(Calendar.MINUTE),
                                true
                            ).show()
                        },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Heure",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Heure de fin",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.Gray
                            )
                            Text(
                                text = timeFormat.format(editedTime),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                // Apply changes using Task model methods
                task.editTitle(editedTitle)
                task.editDescription(editedDescription)
                task.changeEndDate(editedDate)
                task.changeEndTime(editedTime)
                onSave()
            }) {
                Text("Enregistrer")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(navController: NavController, taskList: TaskList, alarmController: AlarmController) {
    var taskTitle by remember { mutableStateOf("") }
    var taskDescription by remember { mutableStateOf("") }

    // Date et heure de fin de la tâche
    val calendar = Calendar.getInstance()
    var selectedDate by remember { mutableStateOf(calendar.time) }
    var selectedTime by remember { mutableStateOf(calendar.time) }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    val context = LocalContext.current

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

            // Date picker
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val cal = Calendar.getInstance().apply { time = selectedDate }
                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                val newCal = Calendar.getInstance()
                                newCal.set(year, month, dayOfMonth)
                                selectedDate = newCal.time
                            },
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    },
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Date",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Date de fin",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray
                        )
                        Text(
                            text = dateFormat.format(selectedDate),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            // Time picker
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val cal = Calendar.getInstance().apply { time = selectedTime }
                        TimePickerDialog(
                            context,
                            { _, hourOfDay, minute ->
                                val newCal = Calendar.getInstance()
                                newCal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                newCal.set(Calendar.MINUTE, minute)
                                selectedTime = newCal.time
                            },
                            cal.get(Calendar.HOUR_OF_DAY),
                            cal.get(Calendar.MINUTE),
                            true
                        ).show()
                    },
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Heure",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Heure de fin",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray
                        )
                        Text(
                            text = timeFormat.format(selectedTime),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

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
                        val deadline = Calendar.getInstance()
                        val dateCal = Calendar.getInstance().apply { time = selectedDate }
                        val timeCal = Calendar.getInstance().apply { time = selectedTime }

                        deadline.set(
                            dateCal.get(Calendar.YEAR),
                            dateCal.get(Calendar.MONTH),
                            dateCal.get(Calendar.DAY_OF_MONTH),
                            timeCal.get(Calendar.HOUR_OF_DAY),
                            timeCal.get(Calendar.MINUTE),
                            0
                        )
                        deadline.set(Calendar.MILLISECOND, 0)

                        val newTask = Task(
                            title = taskTitle,
                            description = taskDescription,
                            deadline = deadline,
                            state = State.TODO
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