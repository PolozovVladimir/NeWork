package ru.netology.nework.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import ru.netology.nework.dto.*

interface ApiService {
    @POST("api/users/authentication")
    suspend fun login(
        @Query("login") login: String,
        @Query("pass") pass: String
    ): Response<AuthDto>

    @Multipart
    @POST("api/users/registration")
    suspend fun register(
        @Part login: MultipartBody.Part,
        @Part pass: MultipartBody.Part,
        @Part name: MultipartBody.Part
    ): Response<AuthDto>

    @Multipart
    @POST("api/users/registration")
    suspend fun registerWithAvatar(
        @Part login: MultipartBody.Part,
        @Part pass: MultipartBody.Part,
        @Part name: MultipartBody.Part,
        @Part file: MultipartBody.Part
    ): Response<AuthDto>

    @GET("api/posts")
    suspend fun getPosts(): Response<List<PostDto>>

    @GET("api/posts/latest")
    suspend fun getLatestPosts(@Query("count") count: Int): Response<List<PostDto>>

    @GET("api/posts/{id}")
    suspend fun getPostById(@Path("id") id: Long): Response<PostDto>

    @GET("api/posts/{id}/newer")
    suspend fun getNewerPosts(@Path("id") id: Long): Response<List<PostDto>>

    @GET("api/posts/{id}/before")
    suspend fun getPostsBefore(@Path("id") id: Long, @Query("count") count: Int): Response<List<PostDto>>

    @GET("api/posts/{id}/after")
    suspend fun getPostsAfter(@Path("id") id: Long, @Query("count") count: Int): Response<List<PostDto>>

    @POST("api/posts")
    suspend fun createPost(@Body post: CreatePostRequest): Response<PostDto>

    @DELETE("api/posts/{id}")
    suspend fun deletePost(@Path("id") id: Long): Response<Unit>

    @POST("api/posts/{id}/likes")
    suspend fun likePost(@Path("id") id: Long): Response<PostDto>

    @DELETE("api/posts/{id}/likes")
    suspend fun unlikePost(@Path("id") id: Long): Response<PostDto>

    @GET("api/posts/{postId}/comments")
    suspend fun getComments(@Path("postId") postId: Long): Response<List<CommentDto>>

    @POST("api/posts/{postId}/comments")
    suspend fun createComment(
        @Path("postId") postId: Long,
        @Body comment: CreateCommentRequest
    ): Response<CommentDto>

    @DELETE("api/posts/{postId}/comments/{id}")
    suspend fun deleteComment(
        @Path("postId") postId: Long,
        @Path("id") id: Long
    ): Response<Unit>

    @POST("api/posts/{postId}/comments/{id}/likes")
    suspend fun likeComment(
        @Path("postId") postId: Long,
        @Path("id") id: Long
    ): Response<CommentDto>

    @DELETE("api/posts/{postId}/comments/{id}/likes")
    suspend fun unlikeComment(
        @Path("postId") postId: Long,
        @Path("id") id: Long
    ): Response<CommentDto>

    @GET("api/events")
    suspend fun getEvents(): Response<List<EventDto>>

    @GET("api/events/latest")
    suspend fun getLatestEvents(@Query("count") count: Int): Response<List<EventDto>>

    @GET("api/events/{id}")
    suspend fun getEventById(@Path("id") id: Long): Response<EventDto>

    @GET("api/events/{id}/newer")
    suspend fun getNewerEvents(@Path("id") id: Long): Response<List<EventDto>>

    @GET("api/events/{id}/before")
    suspend fun getEventsBefore(@Path("id") id: Long, @Query("count") count: Int): Response<List<EventDto>>

    @GET("api/events/{id}/after")
    suspend fun getEventsAfter(@Path("id") id: Long, @Query("count") count: Int): Response<List<EventDto>>

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

    @GET("api/{authorId}/wall")
    suspend fun getUserWall(@Path("authorId") authorId: Long): Response<List<PostDto>>

    @GET("api/{authorId}/wall/latest")
    suspend fun getUserWallLatest(
        @Path("authorId") authorId: Long,
        @Query("count") count: Int
    ): Response<List<PostDto>>

    @GET("api/{authorId}/wall/{id}")
    suspend fun getUserWallPost(
        @Path("authorId") authorId: Long,
        @Path("id") id: Long
    ): Response<PostDto>

    @GET("api/{authorId}/wall/{id}/newer")
    suspend fun getUserWallNewer(
        @Path("authorId") authorId: Long,
        @Path("id") id: Long
    ): Response<List<PostDto>>

    @GET("api/{authorId}/wall/{id}/before")
    suspend fun getUserWallBefore(
        @Path("authorId") authorId: Long,
        @Path("id") id: Long,
        @Query("count") count: Int
    ): Response<List<PostDto>>

    @GET("api/{authorId}/wall/{id}/after")
    suspend fun getUserWallAfter(
        @Path("authorId") authorId: Long,
        @Path("id") id: Long,
        @Query("count") count: Int
    ): Response<List<PostDto>>

    @POST("api/{authorId}/wall/{id}/likes")
    suspend fun likeUserWallPost(
        @Path("authorId") authorId: Long,
        @Path("id") id: Long
    ): Response<PostDto>

    @DELETE("api/{authorId}/wall/{id}/likes")
    suspend fun unlikeUserWallPost(
        @Path("authorId") authorId: Long,
        @Path("id") id: Long
    ): Response<PostDto>

    @GET("api/{userId}/jobs")
    suspend fun getUserJobs(@Path("userId") userId: Long): Response<List<JobDto>>

    @GET("api/my/wall")
    suspend fun getMyWall(): Response<List<PostDto>>

    @GET("api/my/wall/latest")
    suspend fun getMyWallLatest(@Query("count") count: Int): Response<List<PostDto>>

    @GET("api/my/wall/{id}")
    suspend fun getMyWallPost(@Path("id") id: Long): Response<PostDto>

    @GET("api/my/wall/{id}/newer")
    suspend fun getMyWallNewer(@Path("id") id: Long): Response<List<PostDto>>

    @GET("api/my/wall/{id}/before")
    suspend fun getMyWallBefore(
        @Path("id") id: Long,
        @Query("count") count: Int
    ): Response<List<PostDto>>

    @GET("api/my/wall/{id}/after")
    suspend fun getMyWallAfter(
        @Path("id") id: Long,
        @Query("count") count: Int
    ): Response<List<PostDto>>

    @POST("api/my/wall/{id}/likes")
    suspend fun likeMyWallPost(@Path("id") id: Long): Response<PostDto>

    @DELETE("api/my/wall/{id}/likes")
    suspend fun unlikeMyWallPost(@Path("id") id: Long): Response<PostDto>

    @GET("api/my/jobs")
    suspend fun getMyJobs(): Response<List<JobDto>>

    @POST("api/my/jobs")
    suspend fun createJob(@Body job: CreateJobRequest): Response<JobDto>

    @DELETE("api/my/jobs/{id}")
    suspend fun deleteJob(@Path("id") id: Long): Response<Unit>

    @Multipart
    @POST("api/media")
    suspend fun uploadMedia(@Part file: MultipartBody.Part): Response<MediaResponse>
}



