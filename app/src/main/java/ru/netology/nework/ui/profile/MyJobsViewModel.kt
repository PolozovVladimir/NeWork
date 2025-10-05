package ru.netology.nework.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.model.Job
import ru.netology.nework.repository.JobRepository
import javax.inject.Inject

@HiltViewModel
class MyJobsViewModel @Inject constructor(
    private val jobRepository: JobRepository
) : ViewModel() {

    private val _jobs = MutableLiveData<List<Job>>()
    val jobs: LiveData<List<Job>> = _jobs

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadJobs(userId: Long) {
        viewModelScope.launch {
            try {
                jobRepository.getUserJobs(userId)
                    .onSuccess { jobs ->
                        _jobs.value = jobs
                    }
                    .onFailure { exception ->
                        _error.value = "Ошибка загрузки работы: ${exception.message}"
                    }
            } catch (e: Exception) {
                _error.value = "Ошибка: ${e.message}"
            }
        }
    }
}