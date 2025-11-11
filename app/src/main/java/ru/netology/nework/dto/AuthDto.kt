package ru.netology.nework.dto

data class AuthDto(
    val id: Long,
    val token: String,
    val avatar: String? = null
)