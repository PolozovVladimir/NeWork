package ru.netology.nework.dto

data class JobDto(
    val id: Long,
    val name: String,
    val position: String,
    val start: String,
    val finish: String? = null,
    val link: String? = null
)

data class CreateJobRequest(
    val name: String,
    val position: String,
    val start: String,
    val finish: String? = null,
    val link: String? = null
)



