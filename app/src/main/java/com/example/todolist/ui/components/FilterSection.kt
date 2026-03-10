package com.example.todolist.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.todolist.model.enums.State
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FilterSection(
    visible: Boolean,
    selectedStateFilter: State?,
    onStateFilterChanged: (State?) -> Unit,
    selectedDateFilter: Date?,
    onDateFilterChanged: (Date?) -> Unit,
    selectedTimeFilter: Date?,
    onTimeFilterChanged: (Date?) -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val context = LocalContext.current

    AnimatedVisibility(visible = visible) {
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
                    onClick = { onStateFilterChanged(null) },
                    label = { Text("Tous") }
                )
                State.entries.forEach { state ->
                    FilterChip(
                        selected = selectedStateFilter == state,
                        onClick = {
                            onStateFilterChanged(if (selectedStateFilter == state) null else state)
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
                            if (selectedDateFilter != null) cal.time = selectedDateFilter
                            DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    val newCal = Calendar.getInstance()
                                    newCal.set(year, month, dayOfMonth, 23, 59, 59)
                                    onDateFilterChanged(newCal.time)
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
                                "Avant le ${dateFormat.format(selectedDateFilter)}"
                            else
                                "Filtrer par date",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                if (selectedDateFilter != null) {
                    IconButton(onClick = { onDateFilterChanged(null) }) {
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
                            if (selectedTimeFilter != null) cal.time = selectedTimeFilter
                            TimePickerDialog(
                                context,
                                { _, hourOfDay, minute ->
                                    val newCal = Calendar.getInstance()
                                    newCal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                    newCal.set(Calendar.MINUTE, minute)
                                    onTimeFilterChanged(newCal.time)
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
                                "Avant ${timeFormat.format(selectedTimeFilter)}"
                            else
                                "Filtrer par heure",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                if (selectedTimeFilter != null) {
                    IconButton(onClick = { onTimeFilterChanged(null) }) {
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
                        onStateFilterChanged(null)
                        onDateFilterChanged(null)
                        onTimeFilterChanged(null)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Réinitialiser tous les filtres", color = Color(0xFFFF5252))
                }
            }
        }
    }
}

