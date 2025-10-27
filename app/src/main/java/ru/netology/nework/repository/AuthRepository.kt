package ru.netology.nework.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.netology.nework.dto.AuthDto
import ru.netology.nework.dto.AuthRequest
import ru.netology.nework.dto.RegisterRequest
import ru.netology.nework.api.SimpleHttpClient
import ru.netology.nework.api.ApiService
import ru.netology.nework.model.AuthState
import ru.netology.nework.auth.AuthTokenStorage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val httpClient: SimpleHttpClient,
    private val apiService: ApiService,
    private val tokenStorage: AuthTokenStorage
) {
    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    init {
        val savedToken = tokenStorage.token
        val savedUserId = tokenStorage.userId
        if (!savedToken.isNullOrBlank() && savedUserId != 0L) {
            _authState.value = AuthState(savedUserId, savedToken)
            Log.d("AuthRepository", "Initialized from saved token: ${savedToken.take(10)}...")
        } else {
            Log.d("AuthRepository", "No saved token found")
        }
    }

    suspend fun login(login: String, password: String): Result<AuthDto> {
        return try {
            Log.d("AuthRepository", "Attempting login with Retrofit API (form-urlencoded)")
            val response = apiService.login(login, password)
            
            if (response.isSuccessful) {
                val authResponse = response.body()
                if (authResponse != null) {
                    tokenStorage.userId = authResponse.id
                    tokenStorage.token = authResponse.token
                    _authState.value = AuthState(authResponse.id, authResponse.token)
                    Log.d("AuthRepository", "Login successful, token saved: ${authResponse.token}")
                    Log.d("AuthRepository", "User ID saved: ${authResponse.id}")
                    Result.success(authResponse)
                } else {
                    Log.e("AuthRepository", "Login response body is null")
                    Result.failure(Exception("Empty response from server"))
                }
            } else {
                Log.e("AuthRepository", "Login failed with code: ${response.code()}")
                val errorBody = response.errorBody()?.string() ?: "No error details"
                Log.e("AuthRepository", "Error details: $errorBody")
                Result.failure(Exception("Login failed: ${response.code()}. Details: $errorBody"))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Login exception", e)
            Result.failure(e)
        }
    }

    suspend fun register(login: String, password: String, name: String): Result<AuthDto> {
        return try {
            Log.d("AuthRepository", "Attempting registration with Retrofit API (form-urlencoded)")
            Log.d("AuthRepository", "Registration data: login=$login, password=${password.take(1)}***, name=$name")
            val response = apiService.register(login, password, name)
            
            if (response.isSuccessful) {
                val authResponse = response.body()
                if (authResponse != null) {
                    tokenStorage.userId = authResponse.id
                    tokenStorage.token = authResponse.token
                    _authState.value = AuthState(authResponse.id, authResponse.token)
                    Log.d("AuthRepository", "Registration successful, token saved: ${authResponse.token}")
                    Result.success(authResponse)
                } else {
                    Log.e("AuthRepository", "Registration response body is null")
                    Result.failure(Exception("Empty response from server"))
                }
            } else {
                Log.e("AuthRepository", "Registration failed with code: ${response.code()}")
                val errorBodyString = try {
                    response.errorBody()?.string() ?: "No error body"
                } catch (e: Exception) {
                    "Failed to read error body: ${e.message}"
                }
                Log.e("AuthRepository", "Full error response: $errorBodyString")
                val responseBodyString = try {
                    response.body()?.toString() ?: "No body"
                } catch (e: Exception) {
                    "Failed to read response body: ${e.message}"
                }
                Log.e("AuthRepository", "Full response body: $responseBodyString")
                Log.e("AuthRepository", "Response headers: ${response.headers()}")
                Result.failure(Exception("Registration failed: ${response.code()}. Details: $errorBodyString"))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Registration exception", e)
            Result.failure(e)
        }
    }

    fun logout() {
        tokenStorage.clear()
        _authState.value = AuthState()
    }

    fun isAuthenticated(): Boolean {
        val isAuth = _authState.value.token != null
        Log.d("AuthRepository", "isAuthenticated: $isAuth, token: ${_authState.value.token?.take(10)}...")
        return isAuth
    }

    fun clearTokenForTesting() {
        Log.d("AuthRepository", "Clearing token for testing 403 issues")
        tokenStorage.clear()
        _authState.value = AuthState()
    }
}
