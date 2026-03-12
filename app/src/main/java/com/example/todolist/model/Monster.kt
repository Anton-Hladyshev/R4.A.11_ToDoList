package com.example.todolist.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import android.graphics.Bitmap

data class MonsterData(
    val id: Int,
    val folder: String,
    val frameCount: Int,
    val level: Int,
    val maxHp: Int,
    val xpValue: Int
)

class Monster(val data: MonsterData, val frames: List<Bitmap>) {
    var currentHp by mutableIntStateOf(data.maxHp)
        private set

    fun takeDamage(amount: Int) {
        currentHp = (currentHp - amount).coerceAtLeast(0)
    }

    fun isDead() = currentHp <= 0
}
