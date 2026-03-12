package com.example.todolist.model

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.todolist.R
import com.example.todolist.utils.CsvReader

class SwordShop(context: Context) {
    private val materials = mutableMapOf<Int, SwordMaterial>()
    val swords = mutableListOf<Sword>()
    
    var currentSwordIndex by mutableIntStateOf(0)
        private set

    init {
        loadMaterials(context)
        loadSwords(context)
    }

    private fun loadMaterials(context: Context) {
        val rows = CsvReader.readCsv(context, "swordMaterialInfo.csv", ";")
        rows.forEach { tokens ->
            if (tokens.size >= 4) {
                val id = tokens[0].toIntOrNull()
                val grade = tokens[1]
                val icon = tokens[2]
                val label = tokens[3]
                if (id != null) {
                    materials[id] = SwordMaterial(id, grade, icon, label)
                }
            }
        }
    }

    private fun loadSwords(context: Context) {
        val swordPriceMultiplier =  context.resources.getInteger(R.integer.sword_price)
        val rows = CsvReader.readCsv(context, "swordInfo.csv", ";")
        rows.forEach { tokens ->
            if (tokens.size >= 3) {
                val id = tokens[0].toIntOrNull()
                val materialId = tokens[1].toIntOrNull()
                val swordSrc = tokens[2]
                
                if (id != null && materialId != null) {
                    val material = materials[materialId] ?: SwordMaterial(materialId, "?", "❓", "Inconnu")
                    val price = id * swordPriceMultiplier
                    val imageResId = context.resources.getIdentifier(swordSrc, "drawable", context.packageName)
                    swords.add(Sword(id, material, swordSrc, if (id == 0) 0 else price, imageResId))
                }
            }
        }
    }

    val currentSword: Sword
        get() = swords[currentSwordIndex]

    val nextSword: Sword?
        get() = if (currentSwordIndex + 1 < swords.size) swords[currentSwordIndex + 1] else null

    fun upgradeSword(wallet: Wallet): Boolean {
        val next = nextSword ?: return false
        if (wallet.withdraw(next.price)) {
            currentSwordIndex++
            return true
        }
        return false
    }
}
