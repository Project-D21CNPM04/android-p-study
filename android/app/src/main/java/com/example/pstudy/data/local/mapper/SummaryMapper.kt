package com.example.pstudy.data.local.mapper

import com.example.pstudy.data.local.entity.SummaryEntity
import com.example.pstudy.data.model.Summary

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