package ru.netology.nework.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.model.Post
import ru.netology.nework.repository.AuthRepository
import ru.netology.nework.repository.PostRepository
import javax.inject.Inject

@HiltViewModel
class MyWallViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> = _posts

    fun loadMyPosts() {
        viewModelScope.launch {
            val currentUserId = authRepository.authState.value.id
            if (currentUserId != 0L) {
                _posts.value = emptyList()
            }
        }
    }
}
