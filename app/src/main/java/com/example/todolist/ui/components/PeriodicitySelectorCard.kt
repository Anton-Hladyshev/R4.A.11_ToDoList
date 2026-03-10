package com.example.todolist.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.todolist.model.enums.Periodicity

private fun Periodicity?.displayName(): String = when (this) {
    Periodicity.DAILY -> "Quotidienne"
    Periodicity.WEEKLY -> "Hebdomadaire"
    Periodicity.MONTHLY -> "Mensuelle"
    null -> "Aucune"
}

@Composable
fun PeriodicitySelectorCard(
    selectedPeriodicity: Periodicity?,
    onPeriodicityChanged: (Periodicity?) -> Unit,
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
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Périodicité",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Périodicité",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                    Text(
                        text = selectedPeriodicity.displayName(),
                        style = MaterialTheme.typography.bodyLarge
                    )
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
                            selected = selectedPeriodicity == null,
                            onClick = {
                                onPeriodicityChanged(null)
                                expanded = false
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Aucune", style = MaterialTheme.typography.bodyMedium)
                    }
                    // Periodicity options
                    Periodicity.entries.forEach { periodicity ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedPeriodicity == periodicity,
                                onClick = {
                                    onPeriodicityChanged(periodicity)
                                    expanded = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(periodicity.displayName(), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

