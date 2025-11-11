package ru.netology.nework.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.model.AuthState
import ru.netology.nework.model.User
import ru.netology.nework.repository.AuthRepository
import ru.netology.nework.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    fun loadMyProfile() {
        viewModelScope.launch {
            val currentUserId = authRepository.authState.value.id
            android.util.Log.d("MyProfileViewModel", "Loading profile for user ID: $currentUserId")
            
            if (currentUserId != 0L) {
                userRepository.getUserById(currentUserId)
                    .onSuccess { user ->
                        android.util.Log.d("MyProfileViewModel", "User loaded: ${user.name} (${user.login})")
                        _user.value = user
                    }
                    .onFailure { exception ->
                        android.util.Log.e("MyProfileViewModel", "Failed to load user profile", exception)
                        val fallbackUser = User(
                            id = currentUserId,
                            login = "user",
                            name = "Пользователь",
                            avatar = null
                        )
                        _user.value = fallbackUser
                        android.util.Log.d("MyProfileViewModel", "Using fallback user data")
                    }
            } else {
                android.util.Log.w("MyProfileViewModel", "No user ID available")
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _authState.value = AuthState()
    }
}









