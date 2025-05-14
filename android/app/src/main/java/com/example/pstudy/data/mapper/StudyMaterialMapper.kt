package com.example.pstudy.data.mapper

import com.example.pstudy.data.local.entity.StudyMaterialEntity
import com.example.pstudy.data.model.StudyMaterials

fun StudyMaterialEntity.toDomain(): StudyMaterials {
    return StudyMaterials(
        id = id,
        input = input,
        type = type,
        userId = userId,
        timeStamp = timeStamp,
        languageCode = languageCode,
        title = title,
    )
}

fun StudyMaterials.toEntity(): StudyMaterialEntity {
    return StudyMaterialEntity(
        id = id,
        input = input,
        type = type,
        userId = userId,
        timeStamp = timeStamp,
        languageCode = languageCode,
        title = title,
    )
}