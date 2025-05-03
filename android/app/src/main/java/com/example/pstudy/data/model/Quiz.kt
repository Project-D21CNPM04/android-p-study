package com.example.pstudy.data.model

data class Quiz(
    val id: String,
    val questions: String,
    val choices: List<String>,
    val answer: String,
)
