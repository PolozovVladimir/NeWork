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
            android.util.Log.d("EventRepository", "Creating event: content=${event.content}, datetime=${event.datetime}, type=${event.type}")
            val response = apiService.createEvent(event)
            android.util.Log.d("EventRepository", "Create event response: code=${response.code()}, isSuccessful=${response.isSuccessful}")
            
            if (response.isSuccessful) {
                val createdEvent = response.body()!!.toModel()
                android.util.Log.d("EventRepository", "Event created successfully: id=${createdEvent.id}")
                Result.success(createdEvent)
            } else {
                val errorBodyString = try {
                    response.errorBody()?.string() ?: "No error body"
                } catch (e: Exception) {
                    "Failed to read error body: ${e.message}"
                }
                android.util.Log.e("EventRepository", "Failed to create event: code=${response.code()}, error: $errorBodyString")
                Result.failure(Exception("Failed to create event: ${response.code()}. Error: $errorBodyString"))
            }
        } catch (e: Exception) {
            android.util.Log.e("EventRepository", "Exception creating event", e)
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
            android.util.Log.d("EventRepository", "Liking event: id=$id")
            val response = apiService.likeEvent(id)
            android.util.Log.d("EventRepository", "Like event response: code=${response.code()}, isSuccessful=${response.isSuccessful}")
            if (response.isSuccessful) {
                val updatedEvent = response.body()!!.toModel()
                android.util.Log.d("EventRepository", "Event liked successfully: id=${updatedEvent.id}, likedByMe=${updatedEvent.likedByMe}, likeCount=${updatedEvent.likeOwnerIds.size}")
                Result.success(updatedEvent)
            } else {
                val errorBodyString = try {
                    response.errorBody()?.string() ?: "No error body"
                } catch (e: Exception) {
                    "Failed to read error body: ${e.message}"
                }
                android.util.Log.e("EventRepository", "Failed to like event: code=${response.code()}, error: $errorBodyString")
                Result.failure(Exception("Failed to like event: ${response.code()}. Error: $errorBodyString"))
            }
        } catch (e: Exception) {
            android.util.Log.e("EventRepository", "Exception liking event", e)
            Result.failure(e)
        }
    }

    suspend fun unlikeEvent(id: Long): Result<Event> {
        return try {
            android.util.Log.d("EventRepository", "Unliking event: id=$id")
            val response = apiService.unlikeEvent(id)
            android.util.Log.d("EventRepository", "Unlike event response: code=${response.code()}, isSuccessful=${response.isSuccessful}")
            if (response.isSuccessful) {
                val updatedEvent = response.body()!!.toModel()
                android.util.Log.d("EventRepository", "Event unliked successfully: id=${updatedEvent.id}, likedByMe=${updatedEvent.likedByMe}, likeCount=${updatedEvent.likeOwnerIds.size}")
                Result.success(updatedEvent)
            } else {
                val errorBodyString = try {
                    response.errorBody()?.string() ?: "No error body"
                } catch (e: Exception) {
                    "Failed to read error body: ${e.message}"
                }
                android.util.Log.e("EventRepository", "Failed to unlike event: code=${response.code()}, error: $errorBodyString")
                Result.failure(Exception("Failed to unlike event: ${response.code()}. Error: $errorBodyString"))
            }
        } catch (e: Exception) {
            android.util.Log.e("EventRepository", "Exception unliking event", e)
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



