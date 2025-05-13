package com.example.pstudy.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Folder(
    val id: String,
    val name: String,
    val userId: String,
    val timestamp: Long = 0L,
    val noteCount: Int = 0
) : Parcelable