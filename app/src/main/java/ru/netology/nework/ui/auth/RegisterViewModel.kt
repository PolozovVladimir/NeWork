package ru.netology.nework.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.model.AuthState
import ru.netology.nework.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val authState: LiveData<AuthState> = authRepository.authState.asLiveData()

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun register(login: String, password: String, name: String) {
        viewModelScope.launch {
            authRepository.register(login, password, name)
                .onFailure { exception ->
                    _error.value = "Пользователь с таким логином уже зарегистрирован"
                }
        }
    }
}
