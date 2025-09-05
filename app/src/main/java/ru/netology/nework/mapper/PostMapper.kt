package ru.netology.nework.mapper

import ru.netology.nework.dto.PostDto
import ru.netology.nework.model.Post
import ru.netology.nework.model.Attachment
import ru.netology.nework.model.AttachmentType
import ru.netology.nework.model.Coordinates

fun PostDto.toModel(): Post = Post(
    id = id,
    authorId = authorId,
    author = author,
    authorAvatar = authorAvatar,
    authorJob = authorJob,
    content = content,
    published = published,
    coords = coords?.toModel(),
    link = link,
    mentionIds = mentionIds,
    mentionedMe = mentionedMe,
    likeOwnerIds = likeOwnerIds,
    likedByMe = likedByMe,
    attachment = attachment?.toModel(),
    ownedByMe = ownedByMe
)

fun ru.netology.nework.dto.CoordinatesDto.toModel(): Coordinates = Coordinates(
    lat = lat,
    lng = lng
)

fun ru.netology.nework.dto.AttachmentDto.toModel(): Attachment = Attachment(
    url = url,
    type = AttachmentType.valueOf(type)
)


