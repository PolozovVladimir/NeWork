package ru.netology.nework.mapper

import ru.netology.nework.dto.JobDto
import ru.netology.nework.model.Job

fun JobDto.toModel(): Job = Job(
    id = id,
    name = name,
    position = position,
    start = start,
    finish = finish,
    link = link
)


