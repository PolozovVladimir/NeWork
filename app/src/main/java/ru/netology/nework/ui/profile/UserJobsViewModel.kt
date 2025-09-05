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
class UserJobsViewModel @Inject constructor(
    private val jobRepository: JobRepository
) : ViewModel() {

    private val _jobs = MutableLiveData<List<Job>>()
    val jobs: LiveData<List<Job>> = _jobs

    fun loadUserJobs(userId: Long) {
        viewModelScope.launch {
            jobRepository.getUserJobs(userId)
                .onSuccess { jobs ->
                    _jobs.value = jobs
                }
        }
    }
}





