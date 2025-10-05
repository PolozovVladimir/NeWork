package ru.netology.nework.ui.events

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.model.Event
import ru.netology.nework.repository.EventRepository
import javax.inject.Inject

@HiltViewModel
class EventsViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    val events: LiveData<List<Event>> = eventRepository.getEvents().asLiveData()

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _deletedEventId = MutableLiveData<Long>()
    val deletedEventId: LiveData<Long> = _deletedEventId

    fun likeEvent(eventId: Long) {
        viewModelScope.launch {
            val current = events.value ?: return@launch
            val event = current.find { it.id == eventId } ?: return@launch
            if (event.likedByMe) {
                eventRepository.unlikeEvent(eventId)
                    .onFailure { _error.value = it.message }
            } else {
                eventRepository.likeEvent(eventId)
                    .onFailure { _error.value = it.message }
            }
        }
    }

    fun participateInEvent(eventId: Long) {
        viewModelScope.launch {
            val current = events.value ?: return@launch
            val event = current.find { it.id == eventId } ?: return@launch
            if (event.participatedByMe) {
                eventRepository.unparticipateInEvent(eventId)
                    .onFailure { _error.value = it.message }
            } else {
                eventRepository.participateInEvent(eventId)
                    .onFailure { _error.value = it.message }
            }
        }
    }

    fun deleteEvent(eventId: Long) {
        viewModelScope.launch {
            eventRepository.deleteEvent(eventId)
                .onSuccess { _deletedEventId.value = eventId }
                .onFailure { _error.value = it.message }
        }
    }
}








