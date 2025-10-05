package ru.netology.nework.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.netology.nework.dto.AuthDto
import ru.netology.nework.api.SimpleHttpClient
import ru.netology.nework.model.AuthState
import ru.netology.nework.auth.AuthTokenStorage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val httpClient: SimpleHttpClient,
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
        return httpClient.login(login, password).onSuccess { authResponse ->
            tokenStorage.userId = authResponse.id
            tokenStorage.token = authResponse.token
            _authState.value = AuthState(authResponse.id, authResponse.token)
            Log.d("AuthRepository", "Login successful, token saved: ${authResponse.token}")
            Log.d("AuthRepository", "User ID saved: ${authResponse.id}")
            Log.d("AuthRepository", "Token length: ${authResponse.token.length}")
            Log.d("AuthRepository", "Token starts with: ${authResponse.token.take(20)}")

            val savedToken = tokenStorage.token
            Log.d("AuthRepository", "Token verification - saved: ${savedToken?.take(10)}...")
            Log.d("AuthRepository", "Token verification - length: ${savedToken?.length}")
        }.onFailure { exception ->
            Log.e("AuthRepository", "Login failed", exception)
        }
    }

    suspend fun register(login: String, password: String, name: String): Result<AuthDto> {
        return httpClient.register(login, password, name).onSuccess { authResponse ->
            tokenStorage.userId = authResponse.id
            tokenStorage.token = authResponse.token
            _authState.value = AuthState(authResponse.id, authResponse.token)
            Log.d("AuthRepository", "Registration successful, token saved: ${authResponse.token}")
        }.onFailure { exception ->
            Log.e("AuthRepository", "Registration failed", exception)
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
