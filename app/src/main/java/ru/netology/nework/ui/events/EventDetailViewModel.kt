package ru.netology.nework.ui.events

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.model.Event
import ru.netology.nework.model.User
import ru.netology.nework.repository.EventRepository
import ru.netology.nework.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class EventDetailViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _event = MutableLiveData<Event?>()
    val event: LiveData<Event?> = _event

    private val _speakers = MutableLiveData<List<User>>()
    val speakers: LiveData<List<User>> = _speakers

    private val _participants = MutableLiveData<List<User>>()
    val participants: LiveData<List<User>> = _participants

    fun loadEvent(eventId: Long) {
        viewModelScope.launch {
            eventRepository.getEventById(eventId)
                .onSuccess { event ->
                    _event.value = event
                    loadSpeakers(event.speakerIds)
                    loadParticipants(event.participantsIds)
                }
        }
    }

    private fun loadSpeakers(speakerIds: List<Long>) {
        viewModelScope.launch {
            val users = mutableListOf<User>()
            speakerIds.forEach { userId ->
                userRepository.getUserById(userId)
                    .onSuccess { user ->
                        users.add(user)
                    }
            }
            _speakers.value = users
        }
    }

    private fun loadParticipants(participantIds: List<Long>) {
        viewModelScope.launch {
            val users = mutableListOf<User>()
            participantIds.forEach { userId ->
                userRepository.getUserById(userId)
                    .onSuccess { user ->
                        users.add(user)
                    }
            }
            _participants.value = users
        }
    }

    fun likeEvent() {
        _event.value?.let { currentEvent ->
            viewModelScope.launch {
                if (currentEvent.likedByMe) {
                    eventRepository.unlikeEvent(currentEvent.id)
                        .onSuccess { updatedEvent ->
                            _event.value = updatedEvent
                        }
                } else {
                    eventRepository.likeEvent(currentEvent.id)
                        .onSuccess { updatedEvent ->
                            _event.value = updatedEvent
                        }
                }
            }
        }
    }

    fun participateInEvent() {
        _event.value?.let { currentEvent ->
            viewModelScope.launch {
                if (currentEvent.participatedByMe) {
                    eventRepository.unparticipateInEvent(currentEvent.id)
                        .onSuccess { updatedEvent ->
                            _event.value = updatedEvent
                            loadParticipants(updatedEvent.participantsIds)
                        }
                } else {
                    eventRepository.participateInEvent(currentEvent.id)
                        .onSuccess { updatedEvent ->
                            _event.value = updatedEvent
                            loadParticipants(updatedEvent.participantsIds)
                        }
                }
            }
        }
    }
}





