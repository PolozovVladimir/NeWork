package ru.netology.nework.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nework.api.MediaApiService
import ru.netology.nework.api.MediaResponse
import ru.netology.nework.util.MediaUtils
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRepository @Inject constructor(
    private val mediaApiService: MediaApiService,
    @ApplicationContext private val context: Context
) {

    suspend fun uploadMedia(file: File): Result<MediaResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val requestBody = file.asRequestBody("image/*".toMediaType())
                val multipartBody = MultipartBody.Part.createFormData(
                    "file",
                    file.name,
                    requestBody
                )
                
                val response = mediaApiService.uploadMedia(multipartBody)
                if (response.isSuccessful) {
                    val mediaResponse = response.body()
                    if (mediaResponse != null) {
                        Result.success(mediaResponse)
                    } else {
                        Result.failure(Exception("Empty response body"))
                    }
                } else {
                    Result.failure(Exception("Upload failed: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    fun validateFileSize(uri: android.net.Uri): Boolean {
        return MediaUtils.validateFileSize(context, uri)
    }

    fun copyFileToCache(uri: android.net.Uri): File? {
        return MediaUtils.copyFileToCache(context, uri)
    }
}



