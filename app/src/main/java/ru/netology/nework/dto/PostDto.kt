package ru.netology.nework.dto

data class PostDto(
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String? = null,
    val authorJob: String? = null,
    val content: String,
    val published: String,
    val coords: CoordinatesDto? = null,
    val link: String? = null,
    val mentionIds: List<Long> = emptyList(),
    val mentionedMe: Boolean = false,
    val likeOwnerIds: List<Long> = emptyList(),
    val likedByMe: Boolean = false,
    val attachment: AttachmentDto? = null,
    val ownedByMe: Boolean = false
)

data class CoordinatesDto(
    val lat: Double,
    val lng: Double
)

data class AttachmentDto(
    val url: String,
    val type: String
)

data class CreatePostRequest(
    val content: String,
    val coords: CoordinatesDto? = null,
    val link: String? = null,
    val mentionIds: List<Long> = emptyList(),
    val attachment: AttachmentDto? = null
)



