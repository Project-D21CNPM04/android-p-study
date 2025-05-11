package com.example.pstudy.data.remote.dto

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class NoteDto(
    @SerializedName("id") val id: String = "",
    @SerializedName("input") val input: String = "",
    @SerializedName("timestamp") val timestamp: Long = 0L,
    @SerializedName("title") val title: String = "",
    @SerializedName("type") val type: String = "",
    @SerializedName("user_id") val userId: String = ""
)