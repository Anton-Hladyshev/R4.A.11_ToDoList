package com.example.todolist.model

import com.example.todolist.model.interfaces.Product

data class Sword(
    val id: Int,
    val material: SwordMaterial,
    val swordSrc: String,
    override val price: Int,
    override val image: Int // Resource ID
) : Product
