package com.example.pstudy.data.mapper

import com.example.pstudy.data.local.entity.MindMapEntity
import com.example.pstudy.data.model.MindMap
import com.example.pstudy.data.remote.dto.MindMapDto

fun MindMapEntity.toDomain(): MindMap {
    return MindMap(
        id = id,
        content = content,
        summary = summary
    )
}

fun MindMap.toEntity(studyMaterialId: String): MindMapEntity {
    return MindMapEntity(
        id = id,
        content = content,
        summary = summary,
        studyMaterialId = studyMaterialId
    )
}

fun MindMapDto.toDomain(): MindMap {
    return MindMap(
        id = id,
        content = content,
        summary = summary
    )
}

fun MindMap.toDto(): MindMapDto {
    return MindMapDto(
        id = id,
        content = content,
        summary = summary
    )
}

fun List<MindMapEntity>.toDomainList(): List<MindMap> {
    return map { it.toDomain() }
}

fun List<MindMap>.toEntityList(studyMaterialId: String): List<MindMapEntity> {
    return map { it.toEntity(studyMaterialId) }
}

fun List<MindMapDto>.toDomainList(): List<MindMap> {
    return map { it.toDomain() }
}

fun List<MindMap>.toDtoList(): List<MindMapDto> {
    return map { it.toDto() }
}