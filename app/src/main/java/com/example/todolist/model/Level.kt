package com.example.todolist.model

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.example.todolist.utils.CsvManager

class Level(context: Context, initialLevel: Int = 1) {
    var currentLevel by mutableIntStateOf(initialLevel)
        private set
    
    var currentXp by mutableIntStateOf(0)
        private set

    private val levelDecors = mutableMapOf<Int, String>()
    private val levelGrounds = mutableMapOf<Int, String>()
    private val levelMaxXp = mutableMapOf<Int, Int>()

    init {
        loadLevelsFromCsv(context)
    }

    private fun loadLevelsFromCsv(context: Context) {
        val rows = CsvManager.readFromAssets(context, "levels.csv", ",")
        if (rows.isEmpty()) {
            for (i in 1..9) {
                levelDecors[i] = "full_bg_$i"
                levelGrounds[i] = "ground$i"
                levelMaxXp[i] = i * 1000
            }
        } else {
            rows.forEach { tokens ->
                if (tokens.size >= 4) {
                    val levelNum = tokens[0].toIntOrNull()
                    val decorName = tokens[1]
                    val groundName = tokens[2]
                    val maxXp = tokens[3].toIntOrNull()
                    if (levelNum != null && maxXp != null) {
                        levelDecors[levelNum] = decorName
                        levelGrounds[levelNum] = groundName
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
            if (levelDecors.containsKey(currentLevel + 1)) {
                currentXp -= maxXp
                currentLevel++
            } else {
                currentXp = maxXp // Cap at max level
            }
        }
    }

    fun restoreProgress(level: Int, xp: Int) {
        currentLevel = if (levelDecors.containsKey(level)) level else 1
        currentXp = xp
    }

    fun getLevelMaxXp(): Int {
        return levelMaxXp[currentLevel] ?: 100
    }

    fun getDecorResourceName(): String {
        return levelDecors[currentLevel] ?: "full_bg_1"
    }

    fun getGroundResourceName(): String {
        return levelGrounds[currentLevel] ?: "ground1"
    }
}
