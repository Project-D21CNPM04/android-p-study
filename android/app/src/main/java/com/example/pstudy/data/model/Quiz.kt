package com.example.pstudy.data.model

data class Quiz(
    val id: Int,
    val questions: String,
    val choices: List<String>,
    val answer: String,
)
