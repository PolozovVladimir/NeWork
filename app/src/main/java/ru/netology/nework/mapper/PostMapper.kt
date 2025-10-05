package ru.netology.nework.mapper

import ru.netology.nework.dto.PostDto
import ru.netology.nework.dto.CoordinatesDto
import ru.netology.nework.dto.AttachmentDto
import ru.netology.nework.model.Post
import ru.netology.nework.model.Coordinates
import ru.netology.nework.model.Attachment
import ru.netology.nework.model.AttachmentType

fun PostDto.toModel(): Post = Post(
    id = id,
    authorId = authorId,
    author = author,
    authorAvatar = authorAvatar,
    authorJob = authorJob,
    content = content,
    published = published,
    coords = coords?.toModel(),
    likeOwnerIds = likeOwnerIds,
    likedByMe = likedByMe,
    attachment = attachment?.toModel(),
    link = link,
    ownedByMe = ownedByMe
)

fun CoordinatesDto.toModel(): Coordinates = Coordinates(
    lat = lat,
    lng = lng
)

fun AttachmentDto.toModel(): Attachment = Attachment(
    url = url,
    type = AttachmentType.valueOf(type)
)
