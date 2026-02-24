package com.example.todolist.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.todolist.controller.TodoController
import com.example.todolist.model.Todo

@Composable
fun TodoListScreen(controller: TodoController, modifier: Modifier = Modifier) {
    var newTodoTitle by remember { mutableStateOf("") }

    Column(modifier = modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = newTodoTitle,
                onValueChange = { newTodoTitle = it },
                label = { Text("New task") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                controller.addTodo(newTodoTitle)
                newTodoTitle = ""
            }) {
                Text("Add")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(controller.todos, key = { it.id }) { todo ->
                TodoItem(todo = todo, controller = controller)
            }
        }
    }
}

@Composable
fun TodoItem(todo: Todo, controller: TodoController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = todo.isCompleted,
            onCheckedChange = { controller.toggleCompleted(todo.id) }
        )
        Text(text = todo.title, modifier = Modifier.weight(1f))
        TextButton(onClick = { controller.removeTodo(todo.id) }) {
            Text("Delete")
        }
    }
}

