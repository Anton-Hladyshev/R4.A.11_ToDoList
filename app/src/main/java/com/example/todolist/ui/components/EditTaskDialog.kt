package com.example.todolist.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.todolist.model.Task
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EditTaskDialog(
    task: Task,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    var editedTitle by remember { mutableStateOf(task.title) }
    var editedDescription by remember { mutableStateOf(task.description) }
    var editedDate by remember { mutableStateOf(task.deadline.time) }
    var editedTime by remember { mutableStateOf(task.deadline.time) }
    var editedPeriodicity by remember { mutableStateOf(task.periodicity) }
    var editedPriority by remember { mutableStateOf(task.priority) }

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
                DatePickerCard(
                    label = "Date de fin",
                    date = editedDate,
                    onDateSelected = { editedDate = it },
                    modifier = Modifier
                )

                // Time picker
                TimePickerCard(
                    label = "Heure de fin",
                    time = editedTime,
                    onTimeSelected = { editedTime = it },
                    modifier = Modifier
                )

                // Periodicity selector
                PeriodicitySelectorCard(
                    selectedPeriodicity = editedPeriodicity,
                    onPeriodicityChanged = { editedPeriodicity = it }
                )

                // Priority selector
                PrioritySelectorCard(
                    selectedPriority = editedPriority,
                    onPriorityChanged = { editedPriority = it }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                // Apply changes using Task model methods
                task.editTitle(editedTitle)
                task.editDescription(editedDescription)
                task.updateDeadlineDate(editedDate.time)
                val timeCal = Calendar.getInstance().apply { time = editedTime }
                task.updateDeadlineTime(
                    timeCal.get(Calendar.HOUR_OF_DAY),
                    timeCal.get(Calendar.MINUTE)
                )
                task.changePeriodicity(editedPeriodicity)
                task.changePriority(editedPriority)
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

