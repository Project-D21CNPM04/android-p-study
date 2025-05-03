package com.example.pstudy.data.model

import java.io.Serializable

data class MindMap(
    val id: String,
    val content: String,
    val summary: String,
) : Serializable