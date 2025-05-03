package com.example.pstudy.data.mapper

import com.example.pstudy.data.model.Note
import com.example.pstudy.data.remote.dto.NoteDto

fun NoteDto.toDomain(): Note {
    return Note(
        id = id,
        input = input,
        type = type,
        userId = userId
    )
}

fun Note.toDto(): NoteDto {
    return NoteDto(
        id = id,
        input = input,
        type = type,
        userId = userId
    )
}

fun List<NoteDto>.toDomainList(): List<Note> {
    return map { it.toDomain() }
}

fun List<Note>.toDtoList(): List<NoteDto> {
    return map { it.toDto() }
}