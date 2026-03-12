package com.example.todolist.model

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.example.todolist.utils.CsvReader

class Level(context: Context, initialLevel: Int = 1) {
    var currentLevel by mutableIntStateOf(initialLevel)
        private set
    
    var currentXp by mutableIntStateOf(0)
        private set

    private val levelBackgrounds = mutableMapOf<Int, String>()
    private val levelMaxXp = mutableMapOf<Int, Int>()

    init {
        loadLevelsFromCsv(context)
    }

    private fun loadLevelsFromCsv(context: Context) {
        val rows = CsvReader.readCsv(context, "levels.csv")
        if (rows.isEmpty()) {
            for (i in 1..9) {
                levelBackgrounds[i] = "full_bg_$i"
                levelMaxXp[i] = i * 1000
            }
        } else {
            rows.forEach { tokens ->
                if (tokens.size >= 3) {
                    val levelNum = tokens[0].toIntOrNull()
                    val imageName = tokens[1]
                    val maxXp = tokens[2].toIntOrNull()
                    if (levelNum != null && maxXp != null) {
                        levelBackgrounds[levelNum] = imageName
                        levelMaxXp[levelNum] = maxXp
                    }
                }
            }
        }
    }

    fun addXp(amount: Int) {
        currentXp += amount
        val maxXp = levelMaxXp[currentLevel] ?: return
        
        if (currentXp >= maxXp) {
            if (levelBackgrounds.containsKey(currentLevel + 1)) {
                currentXp -= maxXp
                currentLevel++
            } else {
                currentXp = maxXp // Cap at max level
            }
        }
    }

    fun getLevelMaxXp(): Int {
        return levelMaxXp[currentLevel] ?: 100
    }

    fun getBackgroundResourceName(): String {
        return levelBackgrounds[currentLevel] ?: "full_bg_1"
    }
}
