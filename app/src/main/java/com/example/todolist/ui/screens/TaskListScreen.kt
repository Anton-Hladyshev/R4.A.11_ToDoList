package com.example.todolist.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.todolist.controller.AlarmController
import com.example.todolist.controller.TaskList
import com.example.todolist.model.*
import com.example.todolist.model.enums.Periodicity
import com.example.todolist.model.enums.Priority
import com.example.todolist.model.enums.State
import com.example.todolist.navigation.Routes
import com.example.todolist.ui.components.CoinsView
import com.example.todolist.ui.components.EditTaskDialog
import com.example.todolist.ui.components.FilterSection
import com.example.todolist.ui.components.TaskCard
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Size
import java.util.*
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    navController: NavController,
    taskList: TaskList,
    alarmController: AlarmController,
    wallet: Wallet,
    level: Level,
    swordShop: SwordShop,
    showScaffold: Boolean = true
) {
    var showFilters by remember { mutableStateOf(false) }

    // Konfetti state
    var konfettiParties by remember { mutableStateOf<List<Party>>(emptyList()) }

    // Filter state
    var selectedStateFilter by remember { mutableStateOf<State?>(null) }
    var selectedPriorityFilter by remember { mutableStateOf<Priority?>(null) }
    var selectedPeriodicityFilter by remember { mutableStateOf<Periodicity?>(null) }
    var selectedDateFilter by remember { mutableStateOf<Date?>(null) }
    var selectedTimeFilter by remember { mutableStateOf<Date?>(null) }

    // Edit task state
    var taskToEdit by remember { mutableStateOf<Task?>(null) }
    var refreshCounter by remember { mutableIntStateOf(0) }

    // Build filter and get filtered tasks
    val filter = Filter(
        stateFilter = selectedStateFilter,
        priorityFilter = selectedPriorityFilter,
        periodicityFilter = selectedPeriodicityFilter,
        endDateFilter = selectedDateFilter,
        endTimeFilter = selectedTimeFilter
    )
    @Suppress("UNUSED_EXPRESSION")
    refreshCounter // read to trigger recomposition
    val filteredTasks = if (selectedStateFilter == null && selectedPriorityFilter == null && selectedPeriodicityFilter == null && selectedDateFilter == null && selectedTimeFilter == null) {
        taskList.tasks
    } else {
        taskList.getFilteredTasks(filter)
    }

    val screenContent: @Composable (PaddingValues) -> Unit = { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
            FilterSection(
                visible = showFilters,
                selectedStateFilter = selectedStateFilter,
                onStateFilterChanged = { selectedStateFilter = it },
                selectedPriorityFilter = selectedPriorityFilter,
                onPriorityFilterChanged = { selectedPriorityFilter = it },
                selectedPeriodicityFilter = selectedPeriodicityFilter,
                onPeriodicityFilterChanged = { selectedPeriodicityFilter = it },
                selectedDateFilter = selectedDateFilter,
                onDateFilterChanged = { selectedDateFilter = it },
                selectedTimeFilter = selectedTimeFilter,
                onTimeFilterChanged = { selectedTimeFilter = it }
            )

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
                key(refreshCounter) {
                    if (filteredTasks.isNotEmpty()) {
                        for (task in filteredTasks) {
                            TaskCard(
                                task = task,
                                onTaskCompleted = {
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
                                },
                                onEditClick = {
                                    taskToEdit = task
                                },
                                onDeleteClick = {
                                    taskList.removeTask(task)
                                    refreshCounter++
                                },
                                alarmController = alarmController
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
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
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate(Routes.ADD_TASK) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Créer une nouvelle tâche")
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (showScaffold) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Ma Liste de Tâches") },
                        actions = {
                            IconButton(onClick = { navController.navigate(Routes.MINI_GAME) }) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Mini Jeu")
                            }
                            CoinsView(wallet = wallet, modifier = Modifier.padding(end = 16.dp))
                        }
                    )
                }
            ) { innerPadding ->
                screenContent(innerPadding)
            }
        } else {
            screenContent(PaddingValues(0.dp))
        }

        // Konfetti overlay
        if (konfettiParties.isNotEmpty()) {
            KonfettiView(
                modifier = Modifier.fillMaxSize(),
                parties = konfettiParties
            )
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
