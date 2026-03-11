package com.example.todolist.ui.components

import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.todolist.controller.AlarmController
import com.example.todolist.model.Task
import com.example.todolist.model.enums.Periodicity
import com.example.todolist.model.enums.Priority
import com.example.todolist.model.enums.State
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

private enum class SwipeAnchor { Closed, Open }

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
    var isValidating by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    val density = LocalDensity.current
    val swipeDistance = with(density) { 80.dp.toPx() }
    val decayAnimationSpec = rememberSplineBasedDecay<Float>()

    val anchors = DraggableAnchors {
        SwipeAnchor.Closed at 0f
        SwipeAnchor.Open at swipeDistance
    }

    @Suppress("DEPRECATION")
    val anchoredDraggableState = remember {
        AnchoredDraggableState(
            initialValue = SwipeAnchor.Closed,
            anchors = anchors,
            positionalThreshold = { distance: Float -> distance * 0.5f },
            velocityThreshold = { with(density) { 100.dp.toPx() } },
            snapAnimationSpec = tween(),
            decayAnimationSpec = decayAnimationSpec
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        // Background with trash icon (revealed on swipe)
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color(0xFFFF5252), shape = RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.CenterStart
        ) {
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Supprimer",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // Foreground card (slides right)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset {
                    IntOffset(
                        x = anchoredDraggableState.requireOffset().roundToInt(),
                        y = 0
                    )
                }
                .anchoredDraggable(anchoredDraggableState, Orientation.Horizontal),
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
                    enabled = !isValidating,
                    onCheckedChange = { checked ->
                        if (checked) {
                            isDone = true
                            isValidating = true
                            coroutineScope.launch {
                                task.validate()
                                // After validate: periodic tasks go back to TODO
                                isDone = task.state == State.DONE
                                isValidating = false
                                onTaskCompleted()
                            }
                            alarmController.cancelAlarms(task)
                        } else {
                            isDone = false
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (task.priority != null) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(
                                        color = task.priority!!.color(),
                                        shape = CircleShape
                                    )
                            )
                        }
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isDone) Color.Gray else Color.Black
                        )
                    }
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
                    if (task.periodicity != null) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Périodicité",
                                modifier = Modifier.size(14.dp),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = when (task.periodicity) {
                                    Periodicity.DAILY -> "Quotidienne"
                                    Periodicity.WEEKLY -> "Hebdomadaire"
                                    Periodicity.MONTHLY -> "Mensuelle"
                                    else -> ""
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }

                if (isValidating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else if (!isDone) {
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
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

