package ru.netology.nework.ui.posts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.model.Post
import ru.netology.nework.repository.PostRepository
import javax.inject.Inject

@HiltViewModel
class PostsViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    val posts: LiveData<List<Post>> = postRepository.getPosts().asLiveData()

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun likePost(postId: Long) {
        viewModelScope.launch {
            val currentPosts = posts.value ?: return@launch
            val post = currentPosts.find { it.id == postId } ?: return@launch

            if (post.likedByMe) {
                postRepository.unlikePost(postId)
                    .onFailure { exception ->
                        _error.value = "Ошибка при снятии лайка: ${exception.message}"
                    }
            } else {
                postRepository.likePost(postId)
                    .onFailure { exception ->
                        _error.value = "Ошибка при добавлении лайка: ${exception.message}"
                    }
            }
        }
    }
}
