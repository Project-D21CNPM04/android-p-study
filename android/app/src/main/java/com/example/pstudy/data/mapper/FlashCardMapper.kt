package com.example.pstudy.data.mapper

import com.example.pstudy.data.local.entity.FlashCardEntity
import com.example.pstudy.data.model.Content
import com.example.pstudy.data.model.FlashCard
import com.example.pstudy.data.remote.dto.FlashCardDto

fun FlashCardEntity.toDomain(): FlashCard {
    return FlashCard(
        id = id,
        title = title,
        content = Content(
            front = frontContent,
            back = backContent
        )
    )
}

fun FlashCard.toEntity(studyMaterialId: String): FlashCardEntity {
    return FlashCardEntity(
        id = id,
        studyMaterialId = studyMaterialId,
        title = title,
        frontContent = content.front,
        backContent = content.back
    )
}

fun FlashCardDto.toDomain(): FlashCard {
    return FlashCard(
        id = id,
        title = title,
        content = Content(
            front = content.front,
            back = content.back
        )
    )
}

fun FlashCard.toDto(): FlashCardDto {
    return FlashCardDto(
        id = id,
        title = title,
        content = FlashCardDto.FlashCardContent(
            front = content.front,
            back = content.back
        )
    )
}

fun List<FlashCardEntity>.toDomainList(): List<FlashCard> {
    return map { it.toDomain() }
}

fun List<FlashCard>.toEntityList(studyMaterialId: String): List<FlashCardEntity> {
    return map { it.toEntity(studyMaterialId) }
}

fun List<FlashCardDto>.toDomainList(): List<FlashCard> {
    return map { it.toDomain() }
}

fun List<FlashCard>.toDtoList(): List<FlashCardDto> {
    return map { it.toDto() }
}