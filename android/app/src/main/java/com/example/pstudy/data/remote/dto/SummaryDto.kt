package com.example.pstudy.data.remote.dto

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class SummaryDto(
    @SerializedName("id") val id: String = "",
    @SerializedName("note_id") val noteId: String = "",
    @SerializedName("content") val content: String = ""
)