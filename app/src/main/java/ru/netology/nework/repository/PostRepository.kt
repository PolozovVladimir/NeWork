package ru.netology.nework.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.netology.nework.api.ApiService
import ru.netology.nework.dto.CreatePostRequest
import ru.netology.nework.mapper.toModel
import ru.netology.nework.model.Post
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepository @Inject constructor(
    private val apiService: ApiService
) {
    
    fun getPosts(): Flow<List<Post>> = flow {
        try {
            val response = apiService.getPosts()
            if (response.isSuccessful) {
                emit(response.body()?.map { it.toModel() } ?: emptyList())
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    suspend fun getPostById(id: Long): Result<Post> {
        return try {
            val response = apiService.getPostById(id)
            if (response.isSuccessful) {
                Result.success(response.body()!!.toModel())
            } else {
                Result.failure(Exception("Failed to get post"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createPost(post: CreatePostRequest): Result<Post> {
        return try {
            val response = apiService.createPost(post)
            if (response.isSuccessful) {
                Result.success(response.body()!!.toModel())
            } else {
                Result.failure(Exception("Failed to create post"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun deletePost(id: Long): Result<Unit> {
        return try {
            val response = apiService.deletePost(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete post"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun likePost(id: Long): Result<Post> {
        return try {
            val response = apiService.likePost(id)
            if (response.isSuccessful) {
                Result.success(response.body()!!.toModel())
            } else {
                Result.failure(Exception("Failed to like post"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun unlikePost(id: Long): Result<Post> {
        return try {
            val response = apiService.unlikePost(id)
            if (response.isSuccessful) {
                Result.success(response.body()!!.toModel())
            } else {
                Result.failure(Exception("Failed to unlike post"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserPosts(userId: Long): Result<List<Post>> {
        return try {
            val response = apiService.getUserPosts(userId)
            if (response.isSuccessful) {
                Result.success(response.body()?.map { it.toModel() } ?: emptyList())
            } else {
                Result.failure(Exception("Failed to get user posts"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}



