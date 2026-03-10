package com.example.todolist.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.navigation.NavController
import com.example.todolist.controller.TaskList
import com.example.todolist.model.Filter
import com.example.todolist.model.enums.State
import com.example.todolist.ui.components.TaskCard
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Size
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

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

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val context = LocalContext.current

    // Build filter and get filtered tasks
    val filter = Filter(
        stateFilter = selectedStateFilter,
        endDateFilter = selectedDateFilter,
        endTimeFilter = selectedTimeFilter
    )
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
                        TaskCard(task = task, onTaskCompleted = {
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
}

