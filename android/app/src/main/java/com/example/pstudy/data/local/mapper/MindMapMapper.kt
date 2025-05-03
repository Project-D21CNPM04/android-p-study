package com.example.pstudy.data.local.mapper

import com.example.pstudy.data.local.entity.MindMapEntity
import com.example.pstudy.data.model.MindMap

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