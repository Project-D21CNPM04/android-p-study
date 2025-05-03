package com.example.pstudy.data.local.mapper

import com.example.pstudy.data.local.entity.QuizEntity
import com.example.pstudy.data.model.Quiz

fun QuizEntity.toDomain(): Quiz {
    return Quiz(
        id = id,
        questions = questions,
        choices = choices,
        answer = answer
    )
}

fun Quiz.toEntity(studyMaterialId: String): QuizEntity {
    return QuizEntity(
        id = id,
        questions = questions,
        choices = choices,
        answer = answer,
        studyMaterialId = studyMaterialId
    )
}

fun List<QuizEntity>.toDomainList(): List<Quiz> {
    return map { it.toDomain() }
}

fun List<Quiz>.toEntityList(studyMaterialId: String): List<QuizEntity> {
    return map { it.toEntity(studyMaterialId) }
}