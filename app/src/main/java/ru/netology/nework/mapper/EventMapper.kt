package ru.netology.nework.mapper

import ru.netology.nework.dto.EventDto
import ru.netology.nework.model.Event
import ru.netology.nework.model.EventType
import ru.netology.nework.model.Attachment
import ru.netology.nework.model.AttachmentType
import ru.netology.nework.model.Coordinates

fun EventDto.toModel(): Event = Event(
    id = id,
    authorId = authorId,
    author = author,
    authorAvatar = authorAvatar,
    authorJob = authorJob,
    content = content,
    datetime = datetime,
    published = published,
    coords = coords?.toModel(),
    type = EventType.valueOf(type),
    likeOwnerIds = likeOwnerIds,
    likedByMe = likedByMe,
    speakerIds = speakerIds,
    participantsIds = participantsIds,
    participatedByMe = participatedByMe,
    attachment = attachment?.toModel(),
    link = link,
    ownedByMe = ownedByMe
)


