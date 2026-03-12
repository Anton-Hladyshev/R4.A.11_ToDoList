package com.example.todolist.ui.components

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.todolist.controller.PhotoController
import com.example.todolist.model.PhotoInfo
import com.example.todolist.model.Task
import java.util.*

@Composable
fun EditTaskDialog(
    task: Task,
    photoController: PhotoController,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    val context = LocalContext.current

    var editedTitle by remember { mutableStateOf(task.title) }
    var editedDescription by remember { mutableStateOf(task.description) }
    var editedDate by remember { mutableStateOf(task.deadline.time) }
    var editedTime by remember { mutableStateOf(task.deadline.time) }
    var editedPeriodicity by remember { mutableStateOf(task.periodicity) }
    var editedPriority by remember { mutableStateOf(task.priority) }

    // Existing photos that can be removed
    val existingPhotos = remember { mutableStateListOf<PhotoInfo>().apply { addAll(task.photos) } }
    val photosToRemove = remember { mutableStateListOf<String>() }

    // New photos picked from gallery (not yet saved)
    val newPhotoUris = remember { mutableStateListOf<Uri>() }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        newPhotoUris.addAll(uris)
    }

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

                // ── Photos section ──────────────────────────────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Photos",
                            style = MaterialTheme.typography.titleSmall
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        val visibleExisting = existingPhotos.filter { it.id !in photosToRemove }

                        if (visibleExisting.isNotEmpty() || newPhotoUris.isNotEmpty()) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Existing photos
                                items(visibleExisting, key = { it.id }) { photo ->
                                    val file = java.io.File(context.filesDir, "photos/${photo.fileName}")
                                    Box(modifier = Modifier.size(80.dp)) {
                                        Image(
                                            painter = rememberAsyncImagePainter(file),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .matchParentSize()
                                                .clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                        IconButton(
                                            onClick = { photosToRemove.add(photo.id) },
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Supprimer",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }

                                // Newly picked photos
                                items(newPhotoUris) { uri ->
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
                                            onClick = { newPhotoUris.remove(uri) },
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Supprimer",
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
                            Text("Ajouter une photo")
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                // Apply text changes
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

                // Remove marked photos
                for (photoId in photosToRemove) {
                    photoController.removePhoto(task, photoId)
                }

                // Add new photos
                for (uri in newPhotoUris) {
                    photoController.addPhoto(task, uri)
                }

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

