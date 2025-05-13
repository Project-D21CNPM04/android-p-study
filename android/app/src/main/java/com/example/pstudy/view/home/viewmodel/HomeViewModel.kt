package com.example.pstudy.view.home.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pstudy.R
import com.example.pstudy.data.model.MaterialType.TEXT
import com.example.pstudy.data.model.StudyMaterials
import com.example.pstudy.data.repository.StudyRepository
import com.example.pstudy.view.home.uistate.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.pstudy.data.remote.utils.NetworkResult
import com.example.pstudy.ext.getMaterialType

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: StudyRepository,
) : ViewModel() {
    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState = _homeUiState.asStateFlow()

    init {
        initData()
        fetchStudyMaterials()
    }

    fun getItemTab(context: Context, position: Int) =
        context.getString(_homeUiState.value.tabLayoutItems[position])

    private fun initData() {
        _homeUiState.update {
            it.copy(
                tabLayoutItems = listOf(
                    R.string.home_tab_all,
                    R.string.home_tab_folders,
                    R.string.home_tab_shared
                )
            )
        }
    }

    private fun fetchStudyMaterials() {
        viewModelScope.launch {
            _homeUiState.update { it.copy(isLoading = true, error = null) }

            try {
                fetchRemoteStudyMaterials()
            } catch (e: Exception) {
                _homeUiState.update {
                    it.copy(error = e.message, isLoading = false)
                }
            }
        }
    }

    private fun fetchRemoteStudyMaterials() {
        viewModelScope.launch {
            try {
                val result = repository.getRemoteNoteList()
                if (result is NetworkResult.Success) {
                    val noteDtos = result.data
                    val studyMaterials = noteDtos.map { noteDto ->
                        StudyMaterials(
                            id = noteDto.id,
                            input = noteDto.input,
                            type = noteDto.type.getMaterialType(),
                            userId = noteDto.userId,
                            timeStamp = noteDto.timestamp,
                            languageCode = "en",
                            title = noteDto.title
                        )
                    }

                    studyMaterials.forEach { material ->
                        repository.insertStudyMaterial(material)
                    }

                    _homeUiState.update {
                        it.copy(studyMaterials = studyMaterials, isLoading = false)
                    }
                } else if (result is NetworkResult.Error) {
                    _homeUiState.update {
                        it.copy(
                            error = result.message,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _homeUiState.update {
                    it.copy(error = e.message, isLoading = false)
                }
            }
        }
    }
}