package ru.netology.nework.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.model.Post
import ru.netology.nework.repository.PostRepository
import javax.inject.Inject

@HiltViewModel
class UserWallViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> = _posts

    fun loadUserPosts(userId: Long) {
        viewModelScope.launch {

            _posts.value = emptyList()
        }
    }
}
