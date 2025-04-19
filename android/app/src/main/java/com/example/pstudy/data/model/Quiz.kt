package com.example.pstudy.data.model

data class Quiz(
    val id: Int,
    val question: String,
    val answers: List<Answer>
)

data class Answer(
    val text: String,
    val isCorrect: Boolean
)
