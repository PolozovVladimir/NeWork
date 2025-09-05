package ru.netology.nework.repository

import ru.netology.nework.api.ApiService
import ru.netology.nework.dto.CreateJobRequest
import ru.netology.nework.mapper.toModel
import ru.netology.nework.model.Job
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JobRepository @Inject constructor(
    private val apiService: ApiService
) {
    
    suspend fun getUserJobs(userId: Long): Result<List<Job>> {
        return try {
            val response = apiService.getUserJobs(userId)
            if (response.isSuccessful) {
                Result.success(response.body()?.map { it.toModel() } ?: emptyList())
            } else {
                Result.failure(Exception("Failed to get user jobs"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createJob(job: CreateJobRequest): Result<Job> {
        return try {
            val response = apiService.createJob(job)
            if (response.isSuccessful) {
                Result.success(response.body()!!.toModel())
            } else {
                Result.failure(Exception("Failed to create job"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteJob(id: Long): Result<Unit> {
        return try {
            val response = apiService.deleteJob(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete job"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}



