package com.example.todolist.utils

import android.content.Context
import com.example.todolist.model.Task
import com.example.todolist.model.enums.Periodicity
import com.example.todolist.model.enums.Priority
import com.example.todolist.model.enums.State
import java.io.*
import java.util.*

object CsvManager {
    
    // Standard reading from assets (Read-only)
    fun readFromAssets(context: Context, fileName: String, delimiter: String = ";"): List<List<String>> {
        val result = mutableListOf<List<String>>()
        try {
            val inputStream = context.assets.open(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream))
            reader.readLine() // Skip header
            var line: String? = reader.readLine()
            while (line != null) {
                if (line.isNotBlank()) {
                    val tokens = line.split(delimiter)
                    result.add(tokens.map { it.trim() })
                }
                line = reader.readLine()
            }
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    // Saving tasks to internal storage
    fun saveTasks(context: Context, tasks: List<Task>) {
        val file = File(context.filesDir, "tasks_save.csv")
        try {
            val writer = BufferedWriter(FileWriter(file))
            writer.write("id;title;description;deadline;state;periodicity;priority;isRewarded")
            writer.newLine()
            tasks.forEach { task ->
                val line = "${task.id};${task.title};${task.description};${task.deadline.timeInMillis};${task.state.name};${task.periodicity?.name ?: ""};${task.priority?.name ?: ""};${task.isRewarded}"
                writer.write(line)
                writer.newLine()
            }
            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // Loading tasks from internal storage
    fun loadTasks(context: Context): List<Task> {
        val tasks = mutableListOf<Task>()
        val file = File(context.filesDir, "tasks_save.csv")
        if (!file.exists()) return emptyList()

        try {
            val reader = BufferedReader(FileReader(file))
            reader.readLine() // Skip header
            var line: String? = reader.readLine()
            while (line != null) {
                if (line.isNotBlank()) {
                    val tokens = line.split(";")
                    if (tokens.size >= 7) {
                        val id = tokens[0]
                        val title = tokens[1]
                        val description = tokens[2]
                        val deadlineMillis = tokens[3].toLongOrNull() ?: 0L
                        val state = try { State.valueOf(tokens[4]) } catch (e: Exception) { State.TODO }
                        val periodicity = try { if (tokens[5].isNotEmpty()) Periodicity.valueOf(tokens[5]) else null } catch (e: Exception) { null }
                        val priority = try { if (tokens[6].isNotEmpty()) Priority.valueOf(tokens[6]) else null } catch (e: Exception) { null }
                        val isRewarded = if (tokens.size >= 8) tokens[7].toBoolean() else false

                        val calendar = Calendar.getInstance().apply { timeInMillis = deadlineMillis }
                        tasks.add(Task(id, title, description, calendar, state, periodicity, priority, isRewarded))
                    }
                }
                line = reader.readLine()
            }
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return tasks
    }

    // Saving general game data (Coins, Level, XP, Sword)
    fun saveGameData(context: Context, balance: Int, level: Int, xp: Int, swordIndex: Int) {
        val file = File(context.filesDir, "game_data.txt")
        try {
            val writer = BufferedWriter(FileWriter(file))
            writer.write("balance=$balance")
            writer.newLine()
            writer.write("level=$level")
            writer.newLine()
            writer.write("xp=$xp")
            writer.newLine()
            writer.write("swordIndex=$swordIndex")
            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // Loading general game data
    fun loadGameData(context: Context): Map<String, Int> {
        val data = mutableMapOf<String, Int>()
        val file = File(context.filesDir, "game_data.txt")
        if (!file.exists()) return emptyMap()

        try {
            val reader = BufferedReader(FileReader(file))
            var line: String? = reader.readLine()
            while (line != null) {
                val parts = line.split("=")
                if (parts.size == 2) {
                    data[parts[0]] = parts[1].toIntOrNull() ?: 0
                }
                line = reader.readLine()
            }
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return data
    }
}
