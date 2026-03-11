package com.example.todolist.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue

class Wallet(initialBalance: Int = 0) {
    var balance by mutableIntStateOf(initialBalance)
        private set

    fun deposit(amount: Int) {
        if (amount > 0) {
            balance += amount
        }
    }

    fun withdraw(amount: Int): Boolean {
        return if (amount > 0 && balance >= amount) {
            balance -= amount
            true
        } else {
            false
        }
    }

}
