package ru.netology.nework.ui.posts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.dto.AttachmentDto
import ru.netology.nework.dto.CreatePostRequest
import ru.netology.nework.model.Attachment
import ru.netology.nework.model.AttachmentType
import ru.netology.nework.repository.MediaRepository
import ru.netology.nework.repository.PostRepository
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val mediaRepository: MediaRepository
) : ViewModel() {

    private val _postCreated = MutableLiveData(false)
    val postCreated: LiveData<Boolean> = _postCreated

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _uploading = MutableLiveData<Boolean>()
    val uploading: LiveData<Boolean> = _uploading

    fun createPost(content: String, link: String? = null, attachment: ru.netology.nework.model.Attachment? = null, coordinates: ru.netology.nework.model.Coordinates? = null) {
        viewModelScope.launch {
            val attachmentDto = attachment?.let {
                AttachmentDto(
                    url = it.url,
                    type = it.type.name
                )
            }

            val coordsDto = coordinates?.let {
                ru.netology.nework.dto.CoordinatesDto(
                    lat = it.lat,
                    lng = it.lng
                )
            }

            val request = CreatePostRequest(
                content = content,
                link = link,
                coords = coordsDto,
                mentionIds = emptyList(), // TODO: Add mentions
                attachment = attachmentDto
            )

            postRepository.createPost(request)
                .onSuccess {
                    _postCreated.value = true
                }
                .onFailure { exception ->
                    _error.value = "Ошибка при создании поста: ${exception.message}"
                }
        }
    }

    suspend fun uploadFile(file: File): Result<Attachment> {
        return try {
            _uploading.value = true
            val result = mediaRepository.uploadMedia(file)
            _uploading.value = false
            
            result.map { mediaResponse ->
                Attachment(
                    url = mediaResponse.url,
                    type = AttachmentType.IMAGE // TODO: Determine type based on file extension
                )
            }
        } catch (e: Exception) {
            _uploading.value = false
            Result.failure(e)
        }
    }
}


