package com.example.pstudy.view.input

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pstudy.data.model.StudyMaterials
import com.example.pstudy.data.model.Summary
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

        // YouTube video URL validation patterns
        val youtubePatterns = listOf(
            "^https?://(?:www\\.)?youtube\\.com/watch\\?v=[\\w-]+(&\\S*)?$",
            "^https?://(?:www\\.)?youtu\\.be/[\\w-]+(&\\S*)?$",
            "^https?://(?:www\\.)?youtube\\.com/embed/[\\w-]+(&\\S*)?$",
            "^https?://(?:www\\.)?youtube\\.com/shorts/[\\w-]+(&\\S*)?$"
        )

        return youtubePatterns.any { pattern ->
            Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(link).matches()
        }
    }

    fun isValidText(): Boolean {
        val text = _uiState.value.textInput.trim()
        return text.isNotEmpty() && text.length <= 20000
    }

    fun isValidFile(context: Context, uri: Uri?): Boolean {
        uri ?: return false

        val contentResolver = context.contentResolver

        // Check file type - must be PDF
        val mimeType = contentResolver.getType(uri)
        if (mimeType != "application/pdf") {
            return false
        }

        // Check file size - must be <= 10MB
        val fileDescriptor = contentResolver.openFileDescriptor(uri, "r")
        val fileSize = fileDescriptor?.statSize ?: 0
        fileDescriptor?.close()

        val maxSizeBytes = 10 * 1024 * 1024 // 10MB in bytes

        return fileSize <= maxSizeBytes
    }

    fun saveToDatabase(material: StudyMaterials, summary: Summary) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertStudyMaterial(material)
            repository.insertSummary(summary)
        }
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
                        Log.d("InputViewModel", "Processing file URI: $uri")
                        try {
                            // Pass the URI string directly to repository
                            val response = repository.createFileNoteSummary(uri.toString())
                            Log.d("InputViewModel", "File upload response: $response")
                            _uiState.update { state ->
                                state.copy(
                                    isLoading = false,
                                    responseResult = response
                                )
                            }
                        } catch (e: Exception) {
                            Log.e("InputViewModel", "Error processing file", e)
                            _uiState.update { state ->
                                state.copy(
                                    isLoading = false,
                                    responseResult = NetworkResult.Error("Error processing file: ${e.message}")
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
}

data class InputUiState(
    val inputType: String = INPUT_TYPE_TEXT,
    val linkInput: String = "",
    val textInput: String = "",
    val fileUri: Uri? = null,
    val isLoading: Boolean = false,
    val responseResult: NetworkResult<SummaryDto>? = null
)