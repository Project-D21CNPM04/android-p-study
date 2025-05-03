package com.example.pstudy.data.local.mapper

import com.example.pstudy.data.local.entity.StudyMaterialEntity
import com.example.pstudy.data.model.StudyMaterials

fun StudyMaterialEntity.toDomain(
    summary: com.example.pstudy.data.model.Summary? = null,
    mindMap: com.example.pstudy.data.model.MindMap? = null,
    flashCards: List<com.example.pstudy.data.model.FlashCard>? = null,
    quizzes: List<com.example.pstudy.data.model.Quiz>? = null
): StudyMaterials {
    return StudyMaterials(
        id = id,
        input = input,
        type = type,
        userId = userId,
        summary = summary,
        mindMap = mindMap,
        flashCards = flashCards,
        quizzes = quizzes,
        timeStamp = timeStamp,
        languageCode = languageCode
    )
}

fun StudyMaterials.toEntity(): StudyMaterialEntity {
    return StudyMaterialEntity(
        id = id,
        input = input,
        type = type,
        userId = userId,
        summaryId = summary?.id,
        mindMapId = mindMap?.id,
        timeStamp = timeStamp,
        languageCode = languageCode
    )
}