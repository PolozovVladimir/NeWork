package ru.netology.nework.ui.posts

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.netology.nework.model.Post
import ru.netology.nework.repository.PostRepository
import javax.inject.Inject

@HiltViewModel
class PostsViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> = _posts

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _deletedPostId = MutableLiveData<Long>()
    val deletedPostId: LiveData<Long> = _deletedPostId

    init {
        loadPosts()
    }

    private fun loadPosts() {
        viewModelScope.launch {
            try {
                postRepository.getPosts().collect { postsList ->
                    Log.d("PostsViewModel", "Posts loaded: ${postsList.size} posts")
                    _posts.value = postsList
                }
            } catch (e: Exception) {
                Log.e("PostsViewModel", "Error loading posts", e)
            }
        }
    }

    fun reloadPosts() {
        loadPosts()
    }

    fun likePost(postId: Long) {
        viewModelScope.launch {
            val currentPosts = _posts.value ?: return@launch
            val post = currentPosts.find { it.id == postId } ?: return@launch

            Log.d("PostsViewModel", "Liking post: id=$postId, currentlyLiked=${post.likedByMe}, currentLikeCount=${post.likeOwnerIds.size}")

            if (post.likedByMe) {
                postRepository.unlikePost(postId)
                    .onSuccess { updatedPost ->
                        Log.d("PostsViewModel", "Post unliked successfully: id=${updatedPost.id}, likedByMe=${updatedPost.likedByMe}, newLikeCount=${updatedPost.likeOwnerIds.size}")
                        val updatedPosts = currentPosts.map { if (it.id == postId) updatedPost else it }.toList()
                        Log.d("PostsViewModel", "Updating posts list: ${updatedPosts.size} posts, updated post likeCount=${updatedPost.likeOwnerIds.size}")
                        _posts.value = updatedPosts
                    }
                    .onFailure { exception ->
                        Log.e("PostsViewModel", "Error unliking post", exception)
                        _error.value = "Ошибка при снятии лайка: ${exception.message}"
                    }
            } else {
                postRepository.likePost(postId)
                    .onSuccess { updatedPost ->
                        Log.d("PostsViewModel", "Post liked successfully: id=${updatedPost.id}, likedByMe=${updatedPost.likedByMe}, newLikeCount=${updatedPost.likeOwnerIds.size}")
                        val updatedPosts = currentPosts.map { if (it.id == postId) updatedPost else it }.toList()
                        Log.d("PostsViewModel", "Updating posts list: ${updatedPosts.size} posts, updated post likeCount=${updatedPost.likeOwnerIds.size}")
                        _posts.value = updatedPosts
                    }
                    .onFailure { exception ->
                        Log.e("PostsViewModel", "Error liking post", exception)
                        _error.value = "Ошибка при добавлении лайка: ${exception.message}"
                    }
            }
        }
    }

    fun deletePost(postId: Long) {
        viewModelScope.launch {
            postRepository.deletePost(postId)
                .onSuccess { 
                    _deletedPostId.value = postId
                    reloadPosts()
                }
                .onFailure { exception -> _error.value = "Ошибка удаления: ${exception.message}" }
        }
    }
}
