package com.example.todolist.controller

import android.content.Context
import com.example.todolist.model.Monster
import com.example.todolist.model.MonsterData
import com.example.todolist.utils.AssetLoader
import com.example.todolist.utils.CsvReader

class MonsterManager(private val context: Context) {
    private val monsterList = mutableListOf<MonsterData>()

    init {
        loadMonstersFromCsv()
    }

    private fun loadMonstersFromCsv() {
        val rows = CsvReader.readCsv(context, "monsters.csv", ";")
        rows.forEach { tokens ->
            if (tokens.size >= 5) {
                val id = tokens[0].toIntOrNull()
                val folder = tokens[1]
                val frameCount = tokens[2].toIntOrNull()
                val level = tokens[3].toIntOrNull()
                val hp = tokens[4].toIntOrNull()

                if (id != null && frameCount != null && level != null && hp != null) {
                    monsterList.add(MonsterData(id, folder, frameCount, level, hp, hp / 2))
                }
            }
        }
    }

    fun getRandomMonsterForLevel(level: Int): Monster? {
        val possibleMonsters = monsterList.filter { it.level == level }
        if (possibleMonsters.isEmpty()) return null
        
        val randomData = possibleMonsters.random()
        return loadMonsterImages(randomData)
    }

    private fun loadMonsterImages(data: MonsterData): Monster? {
        val frames = AssetLoader.loadFrames(context, "monsters/${data.folder}")
        return if (frames.isNotEmpty()) {
            Monster(data, frames)
        } else {
            null
        }
    }
}
