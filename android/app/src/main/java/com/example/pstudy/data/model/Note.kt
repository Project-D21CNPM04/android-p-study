package com.example.pstudy.data.model

data class Note(
    val id: String,
    val input: String,
    val type: String,
    val userId: String,
    val timestamp: Long = 0L,
    val title: String = ""
)