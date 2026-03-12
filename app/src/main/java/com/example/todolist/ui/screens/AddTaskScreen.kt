package com.example.todolist.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.todolist.R
import com.example.todolist.controller.AlarmController
import com.example.todolist.controller.PhotoController
import com.example.todolist.controller.TaskList
import com.example.todolist.model.Task
import com.example.todolist.model.enums.State
import com.example.todolist.model.enums.Periodicity
import com.example.todolist.model.enums.Priority
import com.example.todolist.ui.components.DatePickerCard
import com.example.todolist.ui.components.PeriodicitySelectorCard
import com.example.todolist.ui.components.PrioritySelectorCard
import com.example.todolist.ui.components.TimePickerCard
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    navController: NavController,
    taskList: TaskList,
    alarmController: AlarmController,
    photoController: PhotoController
) {
    var taskTitle by remember { mutableStateOf("") }
    var taskDescription by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()
    var selectedDate by remember { mutableStateOf(calendar.time) }
    var selectedTime by remember { mutableStateOf(calendar.time) }
    var selectedPeriodicity by remember { mutableStateOf<Periodicity?>(null) }
    var selectedPriority by remember { mutableStateOf<Priority?>(null) }

    // Collected photo URIs (not yet saved to internal storage)
    val selectedPhotoUris = remember { mutableStateListOf<Uri>() }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        selectedPhotoUris.addAll(uris)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(id = R.string.new_task_title)) }) }
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
                label = { Text(stringResource(id = R.string.title_label)) },
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = taskDescription,
                onValueChange = { taskDescription = it },
                label = { Text(stringResource(id = R.string.description_label)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )

            // Date picker
            DatePickerCard(
                label = stringResource(id = R.string.deadline_date_label),
                date = selectedDate,
                onDateSelected = { selectedDate = it }
            )

            // Time picker
            TimePickerCard(
                label = stringResource(id = R.string.deadline_time_label),
                time = selectedTime,
                onTimeSelected = { selectedTime = it }
            )

            // Periodicity selector
            PeriodicitySelectorCard(
                selectedPeriodicity = selectedPeriodicity,
                onPeriodicityChanged = { selectedPeriodicity = it }
            )

            // Priority selector
            PrioritySelectorCard(
                selectedPriority = selectedPriority,
                onPriorityChanged = { selectedPriority = it }
            )

            // ── Photos section ──────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = stringResource(id = R.string.photos_label),
                        style = MaterialTheme.typography.titleSmall
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (selectedPhotoUris.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(selectedPhotoUris) { uri ->
                                Box(modifier = Modifier.size(80.dp)) {
                                    Image(
                                        painter = rememberAsyncImagePainter(uri),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .matchParentSize()
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    IconButton(
                                        onClick = { selectedPhotoUris.remove(uri) },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = stringResource(id = R.string.remove_photo),
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    OutlinedButton(
                        onClick = { photoPickerLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(id = R.string.add_photo))
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
                    Text(stringResource(id = R.string.back))
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
                            state = State.TODO,
                            periodicity = selectedPeriodicity,
                            priority = selectedPriority
                        )

                        // Save collected photos to internal storage
                        for (uri in selectedPhotoUris) {
                            photoController.addPhoto(newTask, uri)
                        }

                        taskList.addTask(newTask)
                        alarmController.scheduleTaskAlarm(newTask)
                        navController.popBackStack()
                    },
                    modifier = Modifier.weight(1f),
                    enabled = taskTitle.isNotBlank()
                ) {
                    Text(stringResource(id = R.string.validate))
                }
            }
        }
    }
}
