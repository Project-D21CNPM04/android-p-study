package com.example.pstudy.data.model

data class StudyMaterials(
    val id: Int,
    val title: String,
    val description: String,
    val type: MaterialType,
    val summary: String,
    val record: String,
    val mindMap: String,
    val flashCards: List<FlashCard>,
    val quizs: List<Quiz>,
    val timeStamp: String,
    val languageCode: String,
)


enum class MaterialType {
    FILE,
    LINK,
    TEXT
}