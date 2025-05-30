package com.example.pstudy.data.model

import com.example.pstudy.data.remote.dto.SummaryDto
import java.io.Serializable
import java.util.UUID

data class StudyMaterials(
    val id: String,
    val input: String,
    val type: MaterialType,
    val userId: String,
    val timeStamp: Long = System.currentTimeMillis(),
    val languageCode: String = "en",
    val title: String = "",
    val folderId: String? = null
) : Serializable {
    companion object {
        /**
         * Creates a StudyMaterials object from a SummaryDto
         * @param summaryDto The SummaryDto to convert
         * @param type The type of material (FILE, LINK, TEXT)
         * @param input The original input content
         * @param userId The user ID
         * @param title The title of the study material
         */
        fun fromSummaryDto(
            summaryDto: SummaryDto,
            type: MaterialType,
            input: String,
            userId: String,
            title: String = "",
            folderId: String? = null
        ): StudyMaterials {
            val summary = Summary(
                content = summaryDto.content,
                id = summaryDto.id,
                noteId = summaryDto.noteId
            )

            return StudyMaterials(
                id = summaryDto.noteId,
                input = input,
                type = type,
                userId = userId,
                timeStamp = System.currentTimeMillis(),
                languageCode = "en",
                title = title,
                folderId = folderId
            )
        }
    }
}

enum class MaterialType {
    FILE,
    LINK,
    TEXT,
    PHOTO,
    AUDIO,
}