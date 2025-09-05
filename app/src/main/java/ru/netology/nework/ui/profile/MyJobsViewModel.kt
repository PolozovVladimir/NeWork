package ru.netology.nework.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.model.Job
import ru.netology.nework.repository.AuthRepository
import ru.netology.nework.repository.JobRepository
import javax.inject.Inject

@HiltViewModel
class MyJobsViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _jobs = MutableLiveData<List<Job>>()
    val jobs: LiveData<List<Job>> = _jobs

    fun loadMyJobs() {
        viewModelScope.launch {
            val currentUserId = authRepository.authState.value.id
            if (currentUserId != 0L) {
                jobRepository.getUserJobs(currentUserId)
                    .onSuccess { jobs ->
                        _jobs.value = jobs
                    }
            }
        }
    }
}





