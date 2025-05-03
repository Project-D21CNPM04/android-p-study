package com.example.pstudy.data.model

data class FlashCard(
    val id: Int,
    val content: Content,
    val title: String,
)

data class Content(
    val back: String,
    val front: String
)