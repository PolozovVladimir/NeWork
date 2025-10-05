package ru.netology.nework.mapper

import ru.netology.nework.dto.UserDto
import ru.netology.nework.model.User

fun UserDto.toModel(): User = User(
    id = id,
    login = login,
    name = name,
    avatar = avatar
)






