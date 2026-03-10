package com.example.todolist.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DatePickerCard(
    label: String,
    date: Date,
    onDateSelected: (Date) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val context = LocalContext.current

    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                val cal = Calendar.getInstance().apply { time = date }
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        val newCal = Calendar.getInstance()
                        newCal.set(year, month, dayOfMonth)
                        onDateSelected(newCal.time)
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
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
                Text(
                    text = dateFormat.format(date),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun TimePickerCard(
    label: String,
    time: Date,
    onTimeSelected: (Date) -> Unit,
    modifier: Modifier = Modifier
) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val context = LocalContext.current

    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                val cal = Calendar.getInstance().apply { this.time = time }
                TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        val newCal = Calendar.getInstance()
                        newCal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        newCal.set(Calendar.MINUTE, minute)
                        onTimeSelected(newCal.time)
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
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
                Text(
                    text = timeFormat.format(time),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

