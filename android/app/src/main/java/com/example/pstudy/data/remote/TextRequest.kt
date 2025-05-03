package com.example.pstudy.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class TextRequest(
    val text: String
)