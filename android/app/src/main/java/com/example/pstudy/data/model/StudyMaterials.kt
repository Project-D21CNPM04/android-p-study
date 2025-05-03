package com.example.pstudy.data.model

data class StudyMaterials(
    val id: String,
    val input: String,
    val type: MaterialType,
    val userId: String,
    val summary: Summary? = null,
    val mindMap: MindMap? = null,
    val flashCards: List<FlashCard>? = null,
    val quizzes: List<Quiz>? = null,
    val timeStamp: Long = System.currentTimeMillis(),
    val languageCode: String = "en",
)


enum class MaterialType {
    FILE,
    LINK,
    TEXT
}