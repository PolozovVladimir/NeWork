package ru.netology.nework.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object MediaUtils {
    
    const val MAX_FILE_SIZE = 15 * 1024 * 1024 // 15MB
    
    fun createImagePickerIntent(): Intent {
        return Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
        }
    }
    
    fun createCameraIntent(context: Context): Intent {
        return Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    }
    
    fun createAudioPickerIntent(): Intent {
        return Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "audio/*"
        }
    }
    
    fun createVideoPickerIntent(): Intent {
        return Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "video/*"
        }
    }
    
    fun validateFileSize(context: Context, uri: Uri): Boolean {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val size = inputStream?.available() ?: 0
            inputStream?.close()
            size <= MAX_FILE_SIZE
        } catch (e: Exception) {
            false
        }
    }
    
    fun copyFileToCache(context: Context, uri: Uri): File? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val fileName = "temp_${System.currentTimeMillis()}"
            val cacheFile = File(context.cacheDir, fileName)
            
            inputStream?.use { input ->
                FileOutputStream(cacheFile).use { output ->
                    input.copyTo(output)
                }
            }
            cacheFile
        } catch (e: Exception) {
            null
        }
    }
    
    fun getFileExtension(context: Context, uri: Uri): String? {
        return context.contentResolver.getType(uri)?.substringAfter("/")
    }
}



