package ru.netology.nework.ui.events

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.netology.nework.model.Event
import ru.netology.nework.repository.EventRepository
import javax.inject.Inject

@HiltViewModel
class EventsViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _events = MutableLiveData<List<Event>>()
    val events: LiveData<List<Event>> = _events

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _deletedEventId = MutableLiveData<Long>()
    val deletedEventId: LiveData<Long> = _deletedEventId

    init {
        loadEvents()
    }

    private fun loadEvents() {
        viewModelScope.launch {
            eventRepository.getEvents().collect { eventsList ->
                Log.d("EventsViewModel", "Events loaded: ${eventsList.size} events")
                _events.value = eventsList
            }
        }
    }

    fun reloadEvents() {
        loadEvents()
    }

    fun likeEvent(eventId: Long) {
        viewModelScope.launch {
            val current = _events.value ?: return@launch
            val event = current.find { it.id == eventId } ?: return@launch

            Log.d("EventsViewModel", "Liking event: id=$eventId, currentlyLiked=${event.likedByMe}, currentLikeCount=${event.likeOwnerIds.size}")

            if (event.likedByMe) {
                eventRepository.unlikeEvent(eventId)
                    .onSuccess { updatedEvent ->
                        Log.d("EventsViewModel", "Event unliked successfully: id=${updatedEvent.id}, likedByMe=${updatedEvent.likedByMe}, newLikeCount=${updatedEvent.likeOwnerIds.size}")
                        val updatedEvents = current.map { if (it.id == eventId) updatedEvent else it }.toList()
                        Log.d("EventsViewModel", "Updating events list: ${updatedEvents.size} events, updated event likeCount=${updatedEvent.likeOwnerIds.size}")
                        _events.value = updatedEvents
                    }
                    .onFailure { exception ->
                        Log.e("EventsViewModel", "Error unliking event", exception)
                        _error.value = "Ошибка при снятии лайка: ${exception.message}"
                    }
            } else {
                eventRepository.likeEvent(eventId)
                    .onSuccess { updatedEvent ->
                        Log.d("EventsViewModel", "Event liked successfully: id=${updatedEvent.id}, likedByMe=${updatedEvent.likedByMe}, newLikeCount=${updatedEvent.likeOwnerIds.size}")
                        val updatedEvents = current.map { if (it.id == eventId) updatedEvent else it }.toList()
                        Log.d("EventsViewModel", "Updating events list: ${updatedEvents.size} events, updated event likeCount=${updatedEvent.likeOwnerIds.size}")
                        _events.value = updatedEvents
                    }
                    .onFailure { exception ->
                        Log.e("EventsViewModel", "Error liking event", exception)
                        _error.value = "Ошибка при добавлении лайка: ${exception.message}"
                    }
            }
        }
    }

    fun participateInEvent(eventId: Long) {
        viewModelScope.launch {
            val current = _events.value ?: return@launch
            val event = current.find { it.id == eventId } ?: return@launch

            Log.d("EventsViewModel", "Participating in event: id=$eventId, currentlyParticipated=${event.participatedByMe}")

            if (event.participatedByMe) {
                eventRepository.unparticipateInEvent(eventId)
                    .onSuccess { updatedEvent ->
                        Log.d("EventsViewModel", "Unparticipated from event successfully: id=${updatedEvent.id}, participatedByMe=${updatedEvent.participatedByMe}")
                        val updatedEvents = current.map { if (it.id == eventId) updatedEvent else it }.toList()
                        Log.d("EventsViewModel", "Updating events list: ${updatedEvents.size} events, updated event participatedByMe=${updatedEvent.participatedByMe}")
                        _events.value = updatedEvents
                    }
                    .onFailure { exception ->
                        Log.e("EventsViewModel", "Error unparticipating from event", exception)
                        _error.value = "Ошибка при отказе от участия: ${exception.message}"
                    }
            } else {
                eventRepository.participateInEvent(eventId)
                    .onSuccess { updatedEvent ->
                        Log.d("EventsViewModel", "Participated in event successfully: id=${updatedEvent.id}, participatedByMe=${updatedEvent.participatedByMe}")
                        val updatedEvents = current.map { if (it.id == eventId) updatedEvent else it }.toList()
                        Log.d("EventsViewModel", "Updating events list: ${updatedEvents.size} events, updated event participatedByMe=${updatedEvent.participatedByMe}")
                        _events.value = updatedEvents
                    }
                    .onFailure { exception ->
                        Log.e("EventsViewModel", "Error participating in event", exception)
                        _error.value = "Ошибка при участии: ${exception.message}"
                    }
            }
        }
    }

    fun deleteEvent(eventId: Long) {
        viewModelScope.launch {
            eventRepository.deleteEvent(eventId)
                .onSuccess { 
                    _deletedEventId.value = eventId
                    reloadEvents()
                }
                .onFailure { _error.value = it.message }
        }
    }
}








