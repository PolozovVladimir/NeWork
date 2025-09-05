package ru.netology.nework.ui.events

import androidx.lifecycle.LiveData
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

    fun likeEvent(eventId: Long) {
        viewModelScope.launch {
            // TODO: Implement like functionality
        }
    }

    fun participateInEvent(eventId: Long) {
        viewModelScope.launch {
            // TODO: Implement participate functionality
        }
    }
}





