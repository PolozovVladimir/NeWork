package ru.netology.nework.dto

data class CommentDto(
    val id: Long,
    val postId: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String? = null,
    val content: String,
    val published: String,
    val likeOwnerIds: List<Long> = emptyList(),
    val likedByMe: Boolean = false
)



