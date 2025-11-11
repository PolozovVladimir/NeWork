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
            android.util.Log.d("PostRepository", "Starting to fetch posts...")
            val response = apiService.getPosts()
            android.util.Log.d("PostRepository", "Response received: isSuccessful=${response.isSuccessful}, code=${response.code()}")
            
            if (response.isSuccessful) {
                val posts = response.body()?.map { it.toModel() } ?: emptyList()
                android.util.Log.d("PostRepository", "Posts converted successfully: ${posts.size} posts")
                emit(posts)
            } else {
                val code = response.code()
                val errorMessage = when (code) {
                    401 -> "Не авторизован (401) - токен недействителен"
                    403 -> "Доступ запрещен (403) - проблема с API ключом или токеном"
                    else -> "Ошибка получения постов: $code"
                }
                android.util.Log.e("PostRepository", "Failed to get posts: $errorMessage")
                emit(emptyList())
            }
        } catch (e: Exception) {
            android.util.Log.e("PostRepository", "Exception getting posts", e)
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
            android.util.Log.d("PostRepository", "Creating post: content=${post.content}, link=${post.link}")
            val response = apiService.createPost(post)
            android.util.Log.d("PostRepository", "Create post response: code=${response.code()}, isSuccessful=${response.isSuccessful}")
            
            if (response.isSuccessful) {
                val createdPost = response.body()!!.toModel()
                android.util.Log.d("PostRepository", "Post created successfully: id=${createdPost.id}")
                Result.success(createdPost)
            } else {
                val errorBodyString = try {
                    response.errorBody()?.string() ?: "No error body"
                } catch (e: Exception) {
                    "Failed to read error body: ${e.message}"
                }
                android.util.Log.e("PostRepository", "Failed to create post: code=${response.code()}, error: $errorBodyString")
                Result.failure(Exception("Failed to create post: ${response.code()}. Error: $errorBodyString"))
            }
        } catch (e: Exception) {
            android.util.Log.e("PostRepository", "Exception creating post", e)
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
            android.util.Log.d("PostRepository", "Liking post: id=$id")
            val response = apiService.likePost(id)
            android.util.Log.d("PostRepository", "Like post response: code=${response.code()}, isSuccessful=${response.isSuccessful}")
            if (response.isSuccessful) {
                val updatedPost = response.body()!!.toModel()
                android.util.Log.d("PostRepository", "Post liked successfully: id=${updatedPost.id}, likedByMe=${updatedPost.likedByMe}, likeCount=${updatedPost.likeOwnerIds.size}")
                Result.success(updatedPost)
            } else {
                val code = response.code()
                val errorBodyString = try {
                    response.errorBody()?.string() ?: "No error body"
                } catch (e: Exception) {
                    "Failed to read error body: ${e.message}"
                }
                val error = when (code) {
                    401 -> "Не авторизован (401)"
                    403 -> "Доступ запрещен (403)"
                    else -> "Ошибка лайка: code=$code"
                }
                android.util.Log.e("PostRepository", "Failed to like post: code=$code, error: $errorBodyString")
                Result.failure(Exception("$error. Details: $errorBodyString"))
            }
        } catch (e: Exception) {
            android.util.Log.e("PostRepository", "Exception liking post", e)
            Result.failure(e)
        }
    }

    suspend fun unlikePost(id: Long): Result<Post> {
        return try {
            android.util.Log.d("PostRepository", "Unliking post: id=$id")
            val response = apiService.unlikePost(id)
            android.util.Log.d("PostRepository", "Unlike post response: code=${response.code()}, isSuccessful=${response.isSuccessful}")
            if (response.isSuccessful) {
                val updatedPost = response.body()!!.toModel()
                android.util.Log.d("PostRepository", "Post unliked successfully: id=${updatedPost.id}, likedByMe=${updatedPost.likedByMe}, likeCount=${updatedPost.likeOwnerIds.size}")
                Result.success(updatedPost)
            } else {
                val code = response.code()
                val errorBodyString = try {
                    response.errorBody()?.string() ?: "No error body"
                } catch (e: Exception) {
                    "Failed to read error body: ${e.message}"
                }
                val error = when (code) {
                    401 -> "Не авторизован (401)"
                    403 -> "Доступ запрещен (403)"
                    else -> "Ошибка снятия лайка: code=$code"
                }
                android.util.Log.e("PostRepository", "Failed to unlike post: code=$code, error: $errorBodyString")
                Result.failure(Exception("$error. Details: $errorBodyString"))
            }
        } catch (e: Exception) {
            android.util.Log.e("PostRepository", "Exception unliking post", e)
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



