package ru.netology.nework.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import ru.netology.nework.dto.*

interface ApiService {
    @POST("users/authentication")
    suspend fun login(@Body authRequest: AuthRequest): Response<AuthDto>

    @POST("users/registration")
    suspend fun register(@Body authRequest: AuthRequest): Response<AuthDto>

    @GET("posts")
    suspend fun getPosts(): Response<List<PostDto>>

    @GET("posts/{id}")
    suspend fun getPostById(@Path("id") id: Long): Response<PostDto>

    @POST("posts")
    suspend fun createPost(@Body post: CreatePostRequest): Response<PostDto>

    @DELETE("posts/{id}")
    suspend fun deletePost(@Path("id") id: Long): Response<Unit>

    @POST("posts/{id}/likes")
    suspend fun likePost(@Path("id") id: Long): Response<PostDto>

    @DELETE("posts/{id}/likes")
    suspend fun unlikePost(@Path("id") id: Long): Response<PostDto>

    @GET("events")
    suspend fun getEvents(): Response<List<EventDto>>

    @GET("events/{id}")
    suspend fun getEventById(@Path("id") id: Long): Response<EventDto>

    @POST("events")
    suspend fun createEvent(@Body event: CreateEventRequest): Response<EventDto>

    @DELETE("events/{id}")
    suspend fun deleteEvent(@Path("id") id: Long): Response<Unit>

    @POST("events/{id}/likes")
    suspend fun likeEvent(@Path("id") id: Long): Response<EventDto>

    @DELETE("events/{id}/likes")
    suspend fun unlikeEvent(@Path("id") id: Long): Response<EventDto>

    @POST("events/{id}/participants")
    suspend fun participateInEvent(@Path("id") id: Long): Response<EventDto>

    @DELETE("events/{id}/participants")
    suspend fun unparticipateInEvent(@Path("id") id: Long): Response<EventDto>

    @GET("users")
    suspend fun getUsers(): Response<List<UserDto>>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Long): Response<UserDto>

    @GET("users/{id}/wall")
    suspend fun getUserPosts(@Path("id") id: Long): Response<List<PostDto>>

    @GET("users/{id}/jobs")
    suspend fun getUserJobs(@Path("id") id: Long): Response<List<JobDto>>

    @POST("my/jobs")
    suspend fun createJob(@Body job: CreateJobRequest): Response<JobDto>

    @DELETE("my/jobs/{id}")
    suspend fun deleteJob(@Path("id") id: Long): Response<Unit>

    @Multipart
    @POST("media")
    suspend fun uploadMedia(@Part media: MultipartBody.Part): Response<MediaResponse>
}

data class AuthRequest(
    val login: String,
    val password: String
)


data class MediaResponse(
    val id: String,
    val url: String
)
