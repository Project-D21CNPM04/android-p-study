package com.example.pstudy.data.model

import java.io.Serializable

data class FlashCard(
    val id: String,
    val content: Content,
    val title: String
) : Serializable

data class Content(
    val back: String,
    val front: String
) : Serializable