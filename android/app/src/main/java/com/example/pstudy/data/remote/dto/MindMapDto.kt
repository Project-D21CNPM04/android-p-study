package com.example.pstudy.data.remote.dto

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class MindMapDto(
    @SerializedName("id") val id: String = "",
    @SerializedName("content") val content: String = "",
    @SerializedName("summary") val summary: String = ""
)
