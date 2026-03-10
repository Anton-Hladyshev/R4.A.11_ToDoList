package com.example.todolist.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.todolist.controller.AlarmController
import com.example.todolist.model.Task
import com.example.todolist.model.enums.State
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TaskCard(
    modifier: Modifier = Modifier,
    task: Task,
    onTaskCompleted: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    alarmController: AlarmController
) {
    var isDone by remember { mutableStateOf(task.state == State.DONE) }

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
                    uncheckedColor = if (task.state == State.TODO) Color(0xFFFF9800) else Color(0xFFFF5252)
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

