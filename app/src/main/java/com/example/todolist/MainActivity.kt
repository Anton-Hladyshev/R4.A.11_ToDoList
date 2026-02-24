package com.example.todolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.todolist.controller.TaskList
import com.example.todolist.model.Task
import com.example.todolist.model.enums.State
import com.example.todolist.ui.theme.ToDoListTheme
import java.util.Date

class MainActivity : ComponentActivity() {

    val taskList = TaskList()
    val taskTest = Task(0, "Tache 1", state = State.TODO)
    val taskTest1 = Task(1, "Tache 2", state = State.DONE)
    val taskTest2 = Task(2, "Tache 3", state = State.LATE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskList.addTask(taskTest)
        taskList.addTask(taskTest1)
        taskList.addTask(taskTest2)
        enableEdgeToEdge()
        setContent {
            ToDoListTheme {
                AppNavigation(taskList)
            }
        }
    }
}

@Composable
fun Task(modifier: Modifier = Modifier, task: com.example.todolist.model.Task) {
    var isDone by remember { mutableStateOf(task.state == com.example.todolist.model.enums.State.DONE) }

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
                com.example.todolist.model.enums.State.DONE -> Color(0xFF4CAF50) to "DONE"
                com.example.todolist.model.enums.State.TODO -> Color(0xFFFF9800) to "TODO"
                else -> Color(0xFFFF5252) to "LATE" // Cas Late en rouge
            }

            Checkbox(
                checked = isDone,
                onCheckedChange = { checked ->
                    isDone = checked
                    task.state = if (checked) com.example.todolist.model.enums.State.DONE
                    else com.example.todolist.model.enums.State.TODO
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF4CAF50),
                    uncheckedColor = Color(0xFFFF9800)
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

@Composable
fun AppNavigation(taskList: TaskList) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "task_list") {
        composable("task_list") { TaskListScreen(navController, taskList) }
        composable("add_task") { AddTaskScreen(navController, taskList) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(navController: NavController, taskList: TaskList) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Ma Liste de Tâches") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0xFFF1F1F1), shape = RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp) // Espace entre chaque tâche
            ) {
                if (taskList.tasks.isNotEmpty()) {
                    for (task in taskList.tasks) {
                        Task(task = task)
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Aucune tâche pour le moment...",
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("add_task") },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Créer une nouvelle tâche")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(navController: NavController, taskList: TaskList) {
    var taskTitle by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Nouvelle Tâche") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = taskTitle,
                onValueChange = { taskTitle = it },
                label = { Text("Titre de la tâche") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Retour")
                }
                Button(
                    onClick = {
                        val newTask = Task(0, "Tache 1", state = State.TODO)
                        /* Logique à venir */ navController.popBackStack()
                              },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Valider")
                }
            }
        }
    }
}