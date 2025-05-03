package com.example.pstudy.data.model

import com.google.gson.annotations.SerializedName

data class Summary(
    val content: String,
    val id: String,
    @SerializedName("note_id") val noteId: String,
)