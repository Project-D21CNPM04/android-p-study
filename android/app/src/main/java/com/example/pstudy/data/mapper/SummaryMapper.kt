package com.example.pstudy.data.mapper

import com.example.pstudy.data.local.entity.SummaryEntity
import com.example.pstudy.data.model.Summary
import com.example.pstudy.data.remote.dto.SummaryDto

fun SummaryEntity.toDomain(): Summary {
    return Summary(
        id = id,
        content = content,
        noteId = studyMaterialId
    )
}

fun Summary.toEntity(): SummaryEntity {
    return SummaryEntity(
        id = id,
        content = content,
        studyMaterialId = noteId
    )
}

fun SummaryDto.toDomain(): Summary {
    return Summary(
        id = id,
        content = content,
        noteId = noteId
    )
}

fun Summary.toDto(): SummaryDto {
    return SummaryDto(
        id = id,
        content = content,
        noteId = noteId
    )
}

fun List<SummaryEntity>.toDomainList(): List<Summary> {
    return map { it.toDomain() }
}

fun List<Summary>.toEntityList(): List<SummaryEntity> {
    return map { it.toEntity() }
}

fun List<SummaryDto>.toDomainList(): List<Summary> {
    return map { it.toDomain() }
}

fun List<Summary>.toDtoList(): List<SummaryDto> {
    return map { it.toDto() }
}