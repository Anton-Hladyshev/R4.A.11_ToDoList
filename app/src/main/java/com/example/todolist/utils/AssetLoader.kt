package com.example.todolist.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.InputStream

object AssetLoader {
    /**
     * Loads a single bitmap from the assets folder.
     * @param context Android context
     * @param path Path to the image file relative to assets root (e.g., "swords/sword1.png")
     */
    fun loadBitmap(context: Context, path: String): Bitmap? {
        return try {
            val inputStream: InputStream = context.assets.open(path)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Loads a list of bitmaps from a folder in assets, filtered by prefix and sorted numerically.
     * Useful for animations where frames are named f1.png, f2.png, etc.
     */
    fun loadFrames(context: Context, folderPath: String, prefix: String = "f"): List<Bitmap> {
        val assetManager = context.assets
        return try {
            val files = assetManager.list(folderPath)
                ?.filter { it.startsWith(prefix) && (it.endsWith(".png") || it.endsWith(".jpg") || it.endsWith(".webp")) }
                ?.sortedBy { 
                    it.substringAfter(prefix).substringBefore(".").toIntOrNull() ?: 0 
                } ?: emptyList()

            files.mapNotNull { fileName ->
                loadBitmap(context, "$folderPath/$fileName")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
