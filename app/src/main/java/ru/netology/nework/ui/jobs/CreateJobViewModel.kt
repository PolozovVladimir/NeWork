package ru.netology.nework.ui.jobs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.dto.CreateJobRequest
import ru.netology.nework.model.Job
import ru.netology.nework.repository.AuthRepository
import ru.netology.nework.repository.JobRepository
import javax.inject.Inject

@HiltViewModel
class CreateJobViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _jobCreated = MutableLiveData<Boolean>()
    val jobCreated: LiveData<Boolean> = _jobCreated

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun createJob(
        companyName: String,
        position: String,
        startDate: String,
        finishDate: String?,
        link: String?
    ) {
        viewModelScope.launch {
            val currentUserId = authRepository.authState.value.id
            if (currentUserId == 0L) {
                _error.value = "Пользователь не авторизован"
                return@launch
            }

            val newJob = Job(
                id = 0,
                name = companyName,
                position = position,
                start = startDate,
                finish = finishDate,
                link = link
            )

            jobRepository.createJob(CreateJobRequest(
                name = companyName,
                position = position,
                start = startDate,
                finish = finishDate,
                link = link
            ))
                .onSuccess {
                    _jobCreated.value = true
                }
                .onFailure { exception ->
                    _error.value = "Ошибка при создании работы: ${exception.message}"
                    _jobCreated.value = false
                }
        }
    }
}
