package ru.netology.nework.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.netology.nework.dto.AuthDto
import ru.netology.nework.api.AuthRequest
import ru.netology.nework.model.AuthState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ru.netology.nework.api.ApiService
) {
    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    suspend fun login(login: String, password: String): Result<AuthDto> {
        return try {
            val response = apiService.login(AuthRequest(login, password))
            if (response.isSuccessful) {
                val authResponse = response.body()!!
                _authState.value = AuthState(authResponse.id, authResponse.token)
                Result.success(authResponse)
            } else {
                Result.failure(Exception("Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(login: String, password: String, name: String): Result<AuthDto> {
        return try {
            val response = apiService.register(AuthRequest(login, password))
            if (response.isSuccessful) {
                val authResponse = response.body()!!
                _authState.value = AuthState(authResponse.id, authResponse.token)
                Result.success(authResponse)
            } else {
                Result.failure(Exception("Registration failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        _authState.value = AuthState()
    }

    fun isAuthenticated(): Boolean = _authState.value.token != null
}
