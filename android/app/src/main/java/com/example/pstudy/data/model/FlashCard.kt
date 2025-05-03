package com.example.pstudy.data.model

data class FlashCard(
    val id: String,
    val content: Content,
    val title: String,
)

data class Content(
    val back: String,
    val front: String
)