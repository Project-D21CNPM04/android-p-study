package com.example.pstudy.data.remote.dto

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class FlashCardDto(
    @SerializedName("id") val id: String = "",
    @SerializedName("title") val title: String = "",
    @SerializedName("content") val content: FlashCardContent = FlashCardContent()
) {
    @Serializable
    data class FlashCardContent(
        @SerializedName("front") val front: String = "",
        @SerializedName("back") val back: String = ""
    )
}