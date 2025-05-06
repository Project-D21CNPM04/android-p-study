package com.example.pstudy.view.home.uistate

import com.example.pstudy.data.model.StudyMaterials

data class HomeUiState(
    val tabLayoutItems: List<Int> = emptyList(),
    val studyMaterials: List<StudyMaterials> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)