package ru.netology.nework.dto

data class CreatePostRequest(
    val content: String,
    val coords: CoordinatesDto? = null,
    val link: String? = null,
    val mentionIds: List<Long> = emptyList(),
    val attachment: AttachmentDto? = null
)



