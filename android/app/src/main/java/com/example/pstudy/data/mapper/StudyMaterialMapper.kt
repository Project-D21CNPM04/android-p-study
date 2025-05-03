package com.example.pstudy.data.mapper

import com.example.pstudy.data.local.entity.StudyMaterialEntity
import com.example.pstudy.data.model.FlashCard
import com.example.pstudy.data.model.MindMap
import com.example.pstudy.data.model.Quiz
import com.example.pstudy.data.model.StudyMaterials
import com.example.pstudy.data.model.Summary

fun StudyMaterialEntity.toDomain(
    summary: Summary? = null,
    mindMap: MindMap? = null,
    flashCards: List<FlashCard>? = null,
    quizzes: List<Quiz>? = null
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