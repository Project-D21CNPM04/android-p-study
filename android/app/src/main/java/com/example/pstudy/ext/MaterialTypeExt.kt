package com.example.pstudy.ext

import com.example.pstudy.data.model.MaterialType
import com.example.pstudy.view.input.InputActivity

fun String.getMaterialType(): MaterialType {
    return when (this) {
        InputActivity.INPUT_TYPE_FILE -> MaterialType.FILE
        InputActivity.INPUT_TYPE_LINK -> MaterialType.LINK
        InputActivity.INPUT_TYPE_AUDIO -> MaterialType.AUDIO
        InputActivity.INPUT_TYPE_PHOTO -> MaterialType.PHOTO
        else -> MaterialType.TEXT
    }
}