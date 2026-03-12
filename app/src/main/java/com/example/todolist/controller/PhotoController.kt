package com.example.todolist.controller

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.todolist.model.PhotoInfo
import com.example.todolist.model.Task
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

/**
 * Manages storing, retrieving and removing photos that are attached to tasks.
 */
class PhotoController(private val context: Context) {

    private val photos = mutableListOf<PhotoInfo>()
    private val photosDir: File = File(context.filesDir, "photos").also { it.mkdirs() }
    private val metadataFile: File = File(context.filesDir, "photos_metadata.json")

    init {
        loadMetadata()
    }

    /**
     * Save a photo from a content [Uri] (gallery picker / camera capture).
     */
    fun addPhoto(task: Task, uri: Uri): PhotoInfo {
        val id = UUID.randomUUID().toString()
        val fileName = "$id.jpg"
        val destFile = File(photosDir, fileName)

        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(destFile).use { output ->
                input.copyTo(output)
            }
        } ?: throw IllegalStateException("Cannot open input stream for URI: $uri")

        val info = PhotoInfo(
            id = id,
            taskId = task.id,
            fileName = fileName
        )
        photos.add(info)
        task.addPhoto(info)
        saveMetadata()
        return info
    }

    /**
     * Save a photo from an in-memory [Bitmap].
     * Returns the created [PhotoInfo].
     */
    fun addPhotoFromBitmap(task: Task, bitmap: Bitmap, quality: Int = 90): PhotoInfo {
        val id = UUID.randomUUID().toString()
        val fileName = "$id.jpg"
        val destFile = File(photosDir, fileName)

        FileOutputStream(destFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
        }

        val info = PhotoInfo(
            id = id,
            taskId = task.id,
            fileName = fileName
        )
        photos.add(info)
        task.addPhoto(info)
        saveMetadata()
        return info
    }

    /**
     * Remove a single photo by its [photoId].
     */
    fun removePhoto(task: Task, photoId: String) {
        val info = photos.find { it.id == photoId } ?: return
        File(photosDir, info.fileName).delete()
        photos.remove(info)
        task.removePhoto(photoId)
        saveMetadata()
    }

    /**
     * Remove **all** photos belonging to the given task.
     */
    fun removeAllPhotosForTask(task: Task) {
        val taskPhotos = photos.filter { it.taskId == task.id }
        taskPhotos.forEach { info ->
            File(photosDir, info.fileName).delete()
            photos.remove(info)
        }
        task.photos.clear()
        saveMetadata()
    }

    /**
     * Return all [PhotoInfo] entries for the given [taskId].
     */
    fun getPhotosForTask(taskId: String): List<PhotoInfo> {
        return photos.filter { it.taskId == taskId }
    }

    /**
     * Load the actual image as a [Bitmap]. Returns `null` if the file is missing.
     */
    fun loadBitmap(photoInfo: PhotoInfo): Bitmap? {
        val file = File(photosDir, photoInfo.fileName)
        if (!file.exists()) return null
        return BitmapFactory.decodeFile(file.absolutePath)
    }

    /**
     * Return the absolute [File] for a [PhotoInfo] (useful for Coil / Glide loading).
     */
    fun getFile(photoInfo: PhotoInfo): File {
        return File(photosDir, photoInfo.fileName)
    }

    private fun saveMetadata() {
        val jsonArray = JSONArray()
        photos.forEach { info ->
            val obj = JSONObject().apply {
                put("id", info.id)
                put("taskId", info.taskId)
                put("fileName", info.fileName)
                put("timestamp", info.timestamp)
            }
            jsonArray.put(obj)
        }
        metadataFile.writeText(jsonArray.toString())
    }

    private fun loadMetadata() {
        if (!metadataFile.exists()) return
        val json = metadataFile.readText()
        if (json.isBlank()) return
        val jsonArray = JSONArray(json)
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val info = PhotoInfo(
                id = obj.getString("id"),
                taskId = obj.getString("taskId"),
                fileName = obj.getString("fileName"),
                timestamp = obj.getLong("timestamp")
            )
            // Only keep entries whose files still exist on disk
            if (File(photosDir, info.fileName).exists()) {
                photos.add(info)
            }
        }
    }
}




