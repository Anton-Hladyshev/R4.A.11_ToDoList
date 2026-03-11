package com.example.todolist.model

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.example.todolist.utils.CsvReader

class Level(context: Context, initialLevel: Int = 1) {
    var currentLevel by mutableIntStateOf(initialLevel)
        private set

    private val levelBackgrounds = mutableMapOf<Int, String>()

    init {
        loadLevelsFromCsv(context)
    }

    private fun loadLevelsFromCsv(context: Context) {
        val rows = CsvReader.readCsv(context, "levels.csv")
        if (rows.isEmpty()) {
            // Fallback default backgrounds if CSV fails or is empty
            for (i in 1..9) {
                levelBackgrounds[i] = "full_bg_$i"
            }
        } else {
            rows.forEach { tokens ->
                if (tokens.size >= 2) {
                    val levelNum = tokens[0].toIntOrNull()
                    val imageName = tokens[1]
                    if (levelNum != null) {
                        levelBackgrounds[levelNum] = imageName
                    }
                }
            }
        }
    }

    fun levelUp() {
        if (levelBackgrounds.containsKey(currentLevel + 1)) {
            currentLevel++
        }
    }

    fun levelDown() {
        if (currentLevel > 1) {
            currentLevel--
        }
    }

    fun getBackgroundResourceName(): String {
        return levelBackgrounds[currentLevel] ?: "full_bg_1"
    }
}
