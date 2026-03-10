package com.example.todolist

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.todolist.controller.TaskList
import com.example.todolist.model.Filter
import com.example.todolist.model.Task
import com.example.todolist.model.enums.Periodicity
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
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    val taskList = TaskList()
    val taskTest = Task(title =  "Tache 1", state = State.TODO)
    val taskTest1 = Task(title =  "Tache 2", state = State.DONE)
    val taskTest2 = Task(title =  "Tache 3", state = State.LATE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskList.addTask(taskTest)
        taskList.addTask(taskTest1)
        taskList.addTask(taskTest2)
        enableEdgeToEdge()
        setContent {
            ToDoListTheme {
                AppNavigation(taskList)
            }
        }
    }
}

@Composable
fun Task(
    modifier: Modifier = Modifier,
    task: com.example.todolist.model.Task,
    onTaskCompleted: () -> Unit = {},
    onEditClick: () -> Unit = {}
) {
    var isDone by remember { mutableStateOf(task.state == com.example.todolist.model.enums.State.DONE) }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

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
                        onTaskCompleted()   // Callback to call the konfetti animation
                    } else {
                        task.cancel()
                    }
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF4CAF50),
                    uncheckedColor = Color(0xFFFF9800)
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
                        contentDescription = "Date",
                        modifier = Modifier.size(14.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${dateFormat.format(task.endDate)} à ${timeFormat.format(task.endTime)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Modifier",
                    tint = MaterialTheme.colorScheme.primary
                )
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
fun AppNavigation(taskList: TaskList) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "task_list") {
        composable("task_list") { TaskListScreen(navController, taskList) }
        composable("add_task") { AddTaskScreen(navController, taskList) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(navController: NavController, taskList: TaskList) {
    var showFilters by remember { mutableStateOf(false) }

    // Konfetti state
    var konfettiParties by remember { mutableStateOf<List<Party>>(emptyList()) }

    // Filter state
    var selectedStateFilter by remember { mutableStateOf<State?>(null) }
    var selectedDateFilter by remember { mutableStateOf<java.util.Date?>(null) }
    var selectedTimeFilter by remember { mutableStateOf<java.util.Date?>(null) }

    // Edit task state
    var taskToEdit by remember { mutableStateOf<com.example.todolist.model.Task?>(null) }
    var refreshCounter by remember { mutableIntStateOf(0) }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val context = LocalContext.current

    // Build filter and get filtered tasks (refreshCounter triggers recomposition after edits)
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
                                Party(     // Configuration of the konfetti animation overlay
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
                        }, onEditClick = {
                            taskToEdit = task
                        })
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
    var editedState by remember { mutableStateOf(task.state) }
    var editedPeriodicity by remember { mutableStateOf(task.periodicity) }
    var editedDate by remember { mutableStateOf(task.endDate) }
    var editedTime by remember { mutableStateOf(task.endTime) }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val context = LocalContext.current

    var stateExpanded by remember { mutableStateOf(false) }
    var periodicityExpanded by remember { mutableStateOf(false) }

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

                // State dropdown
                ExposedDropdownMenuBox(
                    expanded = stateExpanded,
                    onExpandedChange = { stateExpanded = it }
                ) {
                    OutlinedTextField(
                        value = editedState.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("État") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stateExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = stateExpanded,
                        onDismissRequest = { stateExpanded = false }
                    ) {
                        State.entries.forEach { state ->
                            DropdownMenuItem(
                                text = { Text(state.name) },
                                onClick = {
                                    editedState = state
                                    stateExpanded = false
                                }
                            )
                        }
                    }
                }

                // Periodicity dropdown
                ExposedDropdownMenuBox(
                    expanded = periodicityExpanded,
                    onExpandedChange = { periodicityExpanded = it }
                ) {
                    OutlinedTextField(
                        value = editedPeriodicity?.name ?: "Aucune",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Périodicité") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = periodicityExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = periodicityExpanded,
                        onDismissRequest = { periodicityExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Aucune") },
                            onClick = {
                                editedPeriodicity = null
                                periodicityExpanded = false
                            }
                        )
                        Periodicity.entries.forEach { periodicity ->
                            DropdownMenuItem(
                                text = { Text(periodicity.name) },
                                onClick = {
                                    editedPeriodicity = periodicity
                                    periodicityExpanded = false
                                }
                            )
                        }
                    }
                }

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
                task.changeState(editedState)
                task.changePeriodicity(editedPeriodicity)
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
fun AddTaskScreen(navController: NavController, taskList: TaskList) {
    var taskTitle by remember { mutableStateOf("") }

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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = taskTitle,
                onValueChange = { taskTitle = it },
                label = { Text("Titre de la tâche") },
                modifier = Modifier.fillMaxWidth()
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
                                val newTask = Task(
                                    title = taskTitle,
                                    state = State.TODO,
                                    endDate = selectedDate,
                                    endTime = selectedTime
                                )
                                taskList.addTask(newTask)
                                navController.popBackStack()
                              },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Valider")
                }
            }
        }
    }
}