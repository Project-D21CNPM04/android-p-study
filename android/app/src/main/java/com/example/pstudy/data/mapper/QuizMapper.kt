package com.example.pstudy.data.mapper

import com.example.pstudy.data.local.entity.QuizEntity
import com.example.pstudy.data.model.Quiz
import com.example.pstudy.data.remote.dto.QuizDto

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

fun QuizDto.toDomain(): Quiz {
    return Quiz(
        id = id,
        questions = question,
        choices = choices,
        answer = answer
    )
}

fun Quiz.toDto(): QuizDto {
    return QuizDto(
        id = id,
        question = questions,
        choices = choices,
        answer = answer
    )
}

fun List<QuizEntity>.toDomainList(): List<Quiz> {
    return map { it.toDomain() }
}

fun List<Quiz>.toEntityList(studyMaterialId: String): List<QuizEntity> {
    return map { it.toEntity(studyMaterialId) }
}