package com.example.pstudy.data.remote.dto

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class QuizDto(
    @SerializedName("id") val id: String = "",
    @SerializedName("questions") val question: String = "",
    @SerializedName("choices") val choices: List<String> = emptyList(),
    @SerializedName("answer") val answer: String = ""
)