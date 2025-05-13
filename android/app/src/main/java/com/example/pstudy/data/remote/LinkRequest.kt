package com.example.pstudy.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class LinkRequest(
    val link: String
)