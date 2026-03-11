package com.example.todolist.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.todolist.model.enums.Priority

fun Priority.color(): Color = when (this) {
    Priority.CRITICAL -> Color(0xFFE53935)
    Priority.IMPORTANT -> Color(0xFFFF9800)
    Priority.ROUTINE -> Color(0xFFFDD835)
    Priority.SOMEDAY -> Color(0xFF4CAF50)
}

@Composable
fun PriorityFlag(
    priority: Priority,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        color = priority.color()
    ) {
        Text(
            text = priority.title,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun PrioritySelectorCard(
    selectedPriority: Priority?,
    onPriorityChanged: (Priority?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Priorité",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Priorité",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                    if (selectedPriority != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        PriorityFlag(priority = selectedPriority)
                    } else {
                        Text(
                            text = "Aucune",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                TextButton(onClick = { expanded = !expanded }) {
                    Text(if (expanded) "Fermer" else "Changer")
                }
            }

            if (expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // "None" option
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedPriority == null,
                            onClick = {
                                onPriorityChanged(null)
                                expanded = false
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Aucune", style = MaterialTheme.typography.bodyMedium)
                    }
                    // Priority options
                    Priority.entries.forEach { priority ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedPriority == priority,
                                onClick = {
                                    onPriorityChanged(priority)
                                    expanded = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            PriorityFlag(priority = priority)
                        }
                    }
                }
            }
        }
    }
}

