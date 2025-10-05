package ru.netology.nework.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.netology.nework.api.ApiService
import ru.netology.nework.mapper.toModel
import ru.netology.nework.model.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val apiService: ApiService
) {
    
    fun getUsers(): Flow<List<User>> = flow {
        try {
            android.util.Log.d("UserRepository", "Starting to fetch users...")
            val response = apiService.getUsers()
            android.util.Log.d("UserRepository", "Response received: isSuccessful=${response.isSuccessful}, code=${response.code()}")
            
            if (response.isSuccessful) {
                val users = response.body()?.map { it.toModel() } ?: emptyList()
                android.util.Log.d("UserRepository", "Users converted successfully: ${users.size} users")
                emit(users)
            } else {
                val code = response.code()
                val errorMessage = when (code) {
                    401 -> "Не авторизован (401) - токен недействителен"
                    403 -> "Доступ запрещен (403) - проблема с API ключом или токеном"
                    else -> "Ошибка получения пользователей: $code"
                }
                android.util.Log.e("UserRepository", "Failed to get users: $errorMessage")
                emit(emptyList())
            }
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Exception getting users", e)
            emit(emptyList())
        }
    }

    suspend fun getUserById(id: Long): Result<User> {
        return try {
            android.util.Log.d("UserRepository", "Getting user by ID: $id")
            val response = apiService.getUserById(id)
            android.util.Log.d("UserRepository", "Get user response: code=${response.code()}, isSuccessful=${response.isSuccessful}")
            
            if (response.isSuccessful) {
                val user = response.body()!!.toModel()
                android.util.Log.d("UserRepository", "User loaded successfully: ${user.name} (${user.login})")
                Result.success(user)
            } else {
                android.util.Log.e("UserRepository", "Failed to get user: code=${response.code()}")
                Result.failure(Exception("Failed to get user: ${response.code()}"))
            }
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Exception getting user", e)
            Result.failure(e)
        }
    }
}






