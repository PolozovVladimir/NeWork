package ru.netology.nework.ui.events

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.dto.CreateEventRequest
import ru.netology.nework.repository.EventRepository
import javax.inject.Inject

@HiltViewModel
class CreateEventViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _eventCreated = MutableLiveData<Boolean>()
    val eventCreated: LiveData<Boolean> = _eventCreated

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun createEvent(
        content: String,
        link: String?,
        datetime: String,
        type: String,
        coords: ru.netology.nework.model.Coordinates? = null,
        speakerIds: List<Long> = emptyList()
    ) {
        viewModelScope.launch {
            val coordsDto = coords?.let {
                ru.netology.nework.dto.CoordinatesDto(
                    lat = it.lat,
                    lng = it.lng
                )
            }
            
            val request = CreateEventRequest(
                content = content,
                datetime = datetime,
                coords = coordsDto,
                type = type,
                speakerIds = speakerIds,
                attachment = null,
                link = link
            )
            
            eventRepository.createEvent(request)
                .onSuccess {
                    _eventCreated.value = true
                }
                .onFailure { exception ->
                    _error.value = "Ошибка при создании события: ${exception.message}"
                    _eventCreated.value = false
                }
        }
    }
}
