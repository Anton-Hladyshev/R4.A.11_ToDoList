package com.example.todolist.model

import android.graphics.Bitmap
import com.example.todolist.model.interfaces.Product

data class Sword(
    val id: Int,
    val material: SwordMaterial,
    val swordSrc: String,
    override val price: Int,
    override val image: Int, // Placeholder/Resource ID if still needed for some legacy parts
    val damage: Int,
    val bitmap: Bitmap? = null // For loading from assets
) : Product
