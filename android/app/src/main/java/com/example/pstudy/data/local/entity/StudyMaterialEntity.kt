package com.example.pstudy.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.pstudy.data.local.converter.MaterialTypeConverter
import com.example.pstudy.data.model.MaterialType

@Entity(tableName = "study_materials")
@TypeConverters(MaterialTypeConverter::class)
data class StudyMaterialEntity(
    @PrimaryKey
    val id: String,
    val input: String,
    val type: MaterialType,
    val userId: String,
    val timeStamp: Long,
    val languageCode: String
)