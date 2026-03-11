package com.example.todolist.utils

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

object CsvReader {
    fun readCsv(context: Context, fileName: String, delimiter: String = ","): List<List<String>> {
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
}
