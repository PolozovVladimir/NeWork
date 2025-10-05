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
            try {
                android.util.Log.d("CreatePostViewModel", "Creating post: content=$content, link=$link")
                
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
                    mentionIds = emptyList(),
                    attachment = attachmentDto
                )

                android.util.Log.d("CreatePostViewModel", "Sending request to repository")
                postRepository.createPost(request)
                    .onSuccess {
                        android.util.Log.d("CreatePostViewModel", "Post created successfully")
                        _postCreated.value = true
                    }
                    .onFailure { exception ->
                        android.util.Log.e("CreatePostViewModel", "Failed to create post", exception)
                        _error.value = "Ошибка при создании поста: ${exception.message}"
                    }
            } catch (e: Exception) {
                android.util.Log.e("CreatePostViewModel", "Exception in createPost", e)
                _error.value = "Ошибка при создании поста: ${e.message}"
            }
        }
    }

    suspend fun uploadFile(file: File): Result<Attachment> {
        return try {
            _uploading.value = true
            val result = mediaRepository.uploadMedia(file)
            _uploading.value = false
            
            result.map { mediaResponse: ru.netology.nework.dto.MediaResponse ->
                ru.netology.nework.model.Attachment(
                    url = mediaResponse.url,
                    type = ru.netology.nework.model.AttachmentType.IMAGE
                )
            }
        } catch (e: Exception) {
            _uploading.value = false
            Result.failure(e)
        }
    }
}


