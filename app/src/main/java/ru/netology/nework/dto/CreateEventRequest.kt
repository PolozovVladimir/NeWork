package ru.netology.nework.dto

data class CreateEventRequest(
    val content: String,
    val datetime: String,
    val coords: CoordinatesDto? = null,
    val type: String,
    val speakerIds: List<Long> = emptyList(),
    val attachment: AttachmentDto? = null,
    val link: String? = null
)



