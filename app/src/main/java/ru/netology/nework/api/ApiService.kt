package ru.netology.nework.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import ru.netology.nework.dto.*

interface ApiService {
    @FormUrlEncoded
    @POST("api/users/authentication")
    suspend fun login(
        @Field("login") login: String,
        @Field("password") password: String
    ): Response<AuthDto>

    @FormUrlEncoded
    @POST("api/users/registration")
    suspend fun register(
        @Field("login") login: String,
        @Field("password") password: String,
        @Field("name") name: String
    ): Response<AuthDto>

    @GET("api/posts")
    suspend fun getPosts(): Response<List<PostDto>>

    @GET("api/posts/{id}")
    suspend fun getPostById(@Path("id") id: Long): Response<PostDto>

    @POST("api/posts")
    suspend fun createPost(@Body post: CreatePostRequest): Response<PostDto>

    @DELETE("api/posts/{id}")
    suspend fun deletePost(@Path("id") id: Long): Response<Unit>

    @POST("api/posts/{id}/likes")
    suspend fun likePost(@Path("id") id: Long): Response<PostDto>

    @DELETE("api/posts/{id}/likes")
    suspend fun unlikePost(@Path("id") id: Long): Response<PostDto>

    @GET("api/events")
    suspend fun getEvents(): Response<List<EventDto>>

    @GET("api/events/{id}")
    suspend fun getEventById(@Path("id") id: Long): Response<EventDto>

    @POST("api/events")
    suspend fun createEvent(@Body event: CreateEventRequest): Response<EventDto>

    @DELETE("api/events/{id}")
    suspend fun deleteEvent(@Path("id") id: Long): Response<Unit>

    @POST("api/events/{id}/likes")
    suspend fun likeEvent(@Path("id") id: Long): Response<EventDto>

    @DELETE("api/events/{id}/likes")
    suspend fun unlikeEvent(@Path("id") id: Long): Response<EventDto>

    @POST("api/events/{id}/participants")
    suspend fun participateInEvent(@Path("id") id: Long): Response<EventDto>

    @DELETE("api/events/{id}/participants")
    suspend fun unparticipateInEvent(@Path("id") id: Long): Response<EventDto>

    @GET("api/users")
    suspend fun getUsers(): Response<List<UserDto>>

    @GET("api/users/{id}")
    suspend fun getUserById(@Path("id") id: Long): Response<UserDto>

    @GET("api/users/{id}/wall")
    suspend fun getUserPosts(@Path("id") id: Long): Response<List<PostDto>>

    @GET("api/users/{id}/jobs")
    suspend fun getUserJobs(@Path("id") id: Long): Response<List<JobDto>>

    @POST("api/my/jobs")
    suspend fun createJob(@Body job: CreateJobRequest): Response<JobDto>

    @DELETE("api/my/jobs/{id}")
    suspend fun deleteJob(@Path("id") id: Long): Response<Unit>

    @Multipart
    @POST("api/media")
    suspend fun uploadMedia(@Part media: MultipartBody.Part): Response<MediaResponse>
}



