package ru.netology.nework.repository

import android.util.Log
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
            Log.d("JobRepository", "Getting jobs for user: $userId")
            val response = apiService.getUserJobs(userId)
            Log.d("JobRepository", "Response: code=${response.code()}, isSuccessful=${response.isSuccessful}")
            if (response.isSuccessful) {
                val jobs = response.body()?.map { it.toModel() } ?: emptyList()
                Log.d("JobRepository", "Jobs loaded successfully: ${jobs.size} jobs")
                Result.success(jobs)
            } else {
                val errorBodyString = try {
                    response.errorBody()?.string() ?: "No error body"
                } catch (e: Exception) {
                    "Failed to read error body: ${e.message}"
                }
                Log.e("JobRepository", "Failed to get user jobs: code=${response.code()}, error: $errorBodyString")
                Result.failure(Exception("Failed to get user jobs: ${response.code()}. Error: $errorBodyString"))
            }
        } catch (e: Exception) {
            Log.e("JobRepository", "Exception getting user jobs", e)
            Result.failure(e)
        }
    }

    suspend fun createJob(job: CreateJobRequest): Result<Job> {
        return try {
            Log.d("JobRepository", "Creating job: name=${job.name}, position=${job.position}")
            val response = apiService.createJob(job)
            Log.d("JobRepository", "Create job response: code=${response.code()}, isSuccessful=${response.isSuccessful}")
            if (response.isSuccessful) {
                val createdJob = response.body()!!.toModel()
                Log.d("JobRepository", "Job created successfully: id=${createdJob.id}")
                Result.success(createdJob)
            } else {
                val errorBodyString = try {
                    response.errorBody()?.string() ?: "No error body"
                } catch (e: Exception) {
                    "Failed to read error body: ${e.message}"
                }
                Log.e("JobRepository", "Failed to create job: code=${response.code()}, error: $errorBodyString")
                Result.failure(Exception("Failed to create job: ${response.code()}. Error: $errorBodyString"))
            }
        } catch (e: Exception) {
            Log.e("JobRepository", "Exception creating job", e)
            Result.failure(e)
        }
    }

    suspend fun deleteJob(id: Long): Result<Unit> {
        return try {
            Log.d("JobRepository", "Deleting job: id=$id")
            val response = apiService.deleteJob(id)
            Log.d("JobRepository", "Delete job response: code=${response.code()}, isSuccessful=${response.isSuccessful}")
            if (response.isSuccessful) {
                Log.d("JobRepository", "Job deleted successfully")
                Result.success(Unit)
            } else {
                val errorBodyString = try {
                    response.errorBody()?.string() ?: "No error body"
                } catch (e: Exception) {
                    "Failed to read error body: ${e.message}"
                }
                Log.e("JobRepository", "Failed to delete job: code=${response.code()}, error: $errorBodyString")
                Result.failure(Exception("Failed to delete job: ${response.code()}. Error: $errorBodyString"))
            }
        } catch (e: Exception) {
            Log.e("JobRepository", "Exception deleting job", e)
            Result.failure(e)
        }
    }
}



