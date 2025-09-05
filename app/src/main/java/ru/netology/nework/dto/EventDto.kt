package ru.netology.nework.dto

data class EventDto(
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String? = null,
    val authorJob: String? = null,
    val content: String,
    val datetime: String,
    val published: String,
    val coords: CoordinatesDto? = null,
    val type: String,
    val likeOwnerIds: List<Long> = emptyList(),
    val likedByMe: Boolean = false,
    val speakerIds: List<Long> = emptyList(),
    val participantsIds: List<Long> = emptyList(),
    val participatedByMe: Boolean = false,
    val attachment: AttachmentDto? = null,
    val link: String? = null,
    val ownedByMe: Boolean = false
)

data class CreateEventRequest(
    val content: String,
    val datetime: String,
    val coords: CoordinatesDto? = null,
    val type: String,
    val speakerIds: List<Long> = emptyList(),
    val attachment: AttachmentDto? = null,
    val link: String? = null
)



