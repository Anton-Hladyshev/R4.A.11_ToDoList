package com.example.todolist.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.todolist.R
import com.example.todolist.controller.AlarmController
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
fun AddTaskScreen(navController: NavController, taskList: TaskList, alarmController: AlarmController) {
    var taskTitle by remember { mutableStateOf("") }
    var taskDescription by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()
    var selectedDate by remember { mutableStateOf(calendar.time) }
    var selectedTime by remember { mutableStateOf(calendar.time) }
    var selectedPeriodicity by remember { mutableStateOf<Periodicity?>(null) }
    var selectedPriority by remember { mutableStateOf<Priority?>(null) }

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
