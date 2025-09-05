package ru.netology.nework.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.netology.nework.api.ApiService
import ru.netology.nework.dto.CreateEventRequest
import ru.netology.nework.mapper.toModel
import ru.netology.nework.model.Event
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    private val apiService: ApiService
) {
    
    fun getEvents(): Flow<List<Event>> = flow {
        try {
            val response = apiService.getEvents()
            if (response.isSuccessful) {
                emit(response.body()?.map { it.toModel() } ?: emptyList())
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    suspend fun getEventById(id: Long): Result<Event> {
        return try {
            val response = apiService.getEventById(id)
            if (response.isSuccessful) {
                Result.success(response.body()!!.toModel())
            } else {
                Result.failure(Exception("Failed to get event"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createEvent(event: CreateEventRequest): Result<Event> {
        return try {
            val response = apiService.createEvent(event)
            if (response.isSuccessful) {
                Result.success(response.body()!!.toModel())
            } else {
                Result.failure(Exception("Failed to create event"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun deleteEvent(id: Long): Result<Unit> {
        return try {
            val response = apiService.deleteEvent(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete event"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun likeEvent(id: Long): Result<Event> {
        return try {
            val response = apiService.likeEvent(id)
            if (response.isSuccessful) {
                Result.success(response.body()!!.toModel())
            } else {
                Result.failure(Exception("Failed to like event"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun unlikeEvent(id: Long): Result<Event> {
        return try {
            val response = apiService.unlikeEvent(id)
            if (response.isSuccessful) {
                Result.success(response.body()!!.toModel())
            } else {
                Result.failure(Exception("Failed to unlike event"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun participateInEvent(id: Long): Result<Event> {
        return try {
            val response = apiService.participateInEvent(id)
            if (response.isSuccessful) {
                Result.success(response.body()!!.toModel())
            } else {
                Result.failure(Exception("Failed to participate in event"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun unparticipateInEvent(id: Long): Result<Event> {
        return try {
            val response = apiService.unparticipateInEvent(id)
            if (response.isSuccessful) {
                Result.success(response.body()!!.toModel())
            } else {
                Result.failure(Exception("Failed to unparticipate in event"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}



