package com.example.pstudy.data.mapper

import com.example.pstudy.data.local.entity.FolderEntity
import com.example.pstudy.data.model.Folder
import javax.inject.Inject

class FolderMapper @Inject constructor() {

    fun mapEntityToModel(entity: FolderEntity, noteCount: Int = 0): Folder {
        return Folder(
            id = entity.id,
            name = entity.name,
            userId = entity.userId,
            timestamp = entity.timestamp,
            noteCount = noteCount
        )
    }

    fun mapModelToEntity(model: Folder): FolderEntity {
        return FolderEntity(
            id = model.id,
            name = model.name,
            userId = model.userId,
            timestamp = model.timestamp
        )
    }
}