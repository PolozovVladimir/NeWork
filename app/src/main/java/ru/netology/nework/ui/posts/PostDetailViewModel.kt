package ru.netology.nework.ui.posts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.model.Post
import ru.netology.nework.model.User
import ru.netology.nework.repository.PostRepository
import ru.netology.nework.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _post = MutableLiveData<Post?>()
    val post: LiveData<Post?> = _post

    private val _mentionedUsers = MutableLiveData<List<User>>()
    val mentionedUsers: LiveData<List<User>> = _mentionedUsers

    fun loadPost(postId: Long) {
        viewModelScope.launch {
            postRepository.getPostById(postId)
                .onSuccess { post ->
                    _post.value = post
                    loadMentionedUsers(post.mentionIds)
                }
        }
    }

    private fun loadMentionedUsers(userIds: List<Long>) {
        viewModelScope.launch {
            val users = mutableListOf<User>()
            userIds.forEach { userId ->
                userRepository.getUserById(userId)
                    .onSuccess { user ->
                        users.add(user)
                    }
            }
            _mentionedUsers.value = users
        }
    }

    fun likePost() {
        _post.value?.let { currentPost ->
            viewModelScope.launch {
                if (currentPost.likedByMe) {
                    postRepository.unlikePost(currentPost.id)
                        .onSuccess { updatedPost ->
                            _post.value = updatedPost
                        }
                } else {
                    postRepository.likePost(currentPost.id)
                        .onSuccess { updatedPost ->
                            _post.value = updatedPost
                        }
                }
            }
        }
    }
}





