package ru.netology.nework.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import ru.netology.nework.dto.MediaResponse

interface MediaApiService {
    
    @Multipart
    @POST("api/media")
    suspend fun uploadMedia(@Part media: MultipartBody.Part): Response<MediaResponse>
}

