package com.example.pstudy.view.input

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pstudy.data.model.StudyMaterials
import com.example.pstudy.data.remote.dto.SummaryDto
import com.example.pstudy.data.remote.utils.NetworkResult
import com.example.pstudy.data.repository.StudyRepository
import com.example.pstudy.view.input.InputActivity.Companion.INPUT_TYPE_FILE
import com.example.pstudy.view.input.InputActivity.Companion.INPUT_TYPE_LINK
import com.example.pstudy.view.input.InputActivity.Companion.INPUT_TYPE_TEXT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class InputViewModel @Inject constructor(
    private val repository: StudyRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(InputUiState())
    val uiState = _uiState.asStateFlow()

    val currentInputType get() = _uiState.value.inputType
    val currentLink get() = _uiState.value.linkInput
    val currentText get() = _uiState.value.textInput
    val selectedFileUri get() = _uiState.value.fileUri
    val isLoading get() = _uiState.value.isLoading
    val responseResult get() = _uiState.value.responseResult

    fun setInputType(type: String) {
        _uiState.update {
            it.copy(inputType = type)
        }
    }

    fun updateLink(link: String) {
        _uiState.update {
            it.copy(linkInput = link)
        }
    }

    fun updateText(text: String) {
        _uiState.update {
            it.copy(textInput = text)
        }
    }

    fun setSelectedFile(uri: Uri) {
        _uiState.update {
            it.copy(fileUri = uri)
        }
    }

    fun isValidLink(): Boolean {
        val link = _uiState.value.linkInput.trim()
        if (link.isEmpty()) return false

        // Simple URL validation pattern
        val urlPattern = Pattern.compile(
            "^(https?|ftp)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
            Pattern.CASE_INSENSITIVE
        )
        return urlPattern.matcher(link).matches()
    }

    fun isValidText(): Boolean {
        val text = _uiState.value.textInput.trim()
        return text.isNotEmpty() && text.length <= 20000
    }

    fun generateStudy(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            // Set loading state to true
            _uiState.update { it.copy(isLoading = true) }

            when (uiState.value.inputType) {
                INPUT_TYPE_TEXT -> {
                    if (isValidText()) {
                        val response = repository.createTextNoteSummary(uiState.value.textInput)
                        Log.d("InputViewModel", "Response: $response")
                        _uiState.update { it.copy(isLoading = false, responseResult = response) }
                    }
                }
                INPUT_TYPE_LINK -> {
                    if (isValidLink()) {
                        val response = repository.createLinkNoteSummary(uiState.value.linkInput)
                        Log.d("InputViewModel", "Response: $response")
                        _uiState.update { it.copy(isLoading = false, responseResult = response) }
                    }
                }
                INPUT_TYPE_FILE -> {
                    uiState.value.fileUri?.let { uri ->
                        // Convert URI to file path
                        val filePath = getFilePathFromUri(context, uri)
                        filePath?.let {
                            val response = repository.createFileNoteSummary(it)
                            Log.d("InputViewModel", "Response: $response")
                            _uiState.update { state ->
                                state.copy(
                                    isLoading = false,
                                    responseResult = response
                                )
                            }
                        }
                    } ?: run {
                        _uiState.update { it.copy(isLoading = false) }
                    }
                }
            }
        }
    }

    private fun getFilePathFromUri(context: Context, uri: Uri): String? {
        val contentResolver = context.contentResolver

        when (uri.scheme) {
            "content" -> {
                val cursor = contentResolver.query(uri, null, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val columnIndex = it.getColumnIndexOrThrow("_data")
                        if (columnIndex >= 0) {
                            return it.getString(columnIndex)
                        }
                    }
                }
                return uri.toString()
            }
            "file" -> {
                return uri.path
            }
            else -> {
                return uri.toString()
            }
        }
    }
}

data class InputUiState(
    val inputType: String = INPUT_TYPE_TEXT,
    val linkInput: String = "",
    val textInput: String = "",
    val fileUri: Uri? = null,
    val isLoading: Boolean = false,
    val responseResult: NetworkResult<SummaryDto>? = null
)