package com.example.pstudy.view.input

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pstudy.data.mapper.toDomain
import com.example.pstudy.data.model.StudyMaterials
import com.example.pstudy.data.model.Summary
import com.example.pstudy.data.remote.dto.SummaryDto
import com.example.pstudy.data.remote.utils.NetworkResult
import com.example.pstudy.data.repository.StudyRepository
import com.example.pstudy.ext.getMaterialType
import com.example.pstudy.view.input.InputActivity.Companion.INPUT_TYPE_AUDIO
import com.example.pstudy.view.input.InputActivity.Companion.INPUT_TYPE_FILE
import com.example.pstudy.view.input.InputActivity.Companion.INPUT_TYPE_LINK
import com.example.pstudy.view.input.InputActivity.Companion.INPUT_TYPE_PHOTO
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
    val selectedPhotoUri get() = _uiState.value.photoUri
    val audioFilePath get() = _uiState.value.audioPath

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

    fun setSelectedPhoto(uri: Uri) {
        _uiState.update {
            it.copy(photoUri = uri)
        }
    }

    fun setAudioFile(path: String) {
        _uiState.update {
            it.copy(audioPath = path)
        }
    }

    fun isValidLink(): Boolean {
        val link = _uiState.value.linkInput.trim()
        return link.isNotEmpty()
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
            //repository.insertStudyMaterial(material)
            val noteId = material.id
            noteId.let { id ->
                val remoteNoteDetail = repository.getRemoteNoteDetail(id)
                if (remoteNoteDetail is NetworkResult.Success) {
                    val noteDto = remoteNoteDetail.data
                    val localNoteDetail = StudyMaterials(
                        id = noteDto.id,
                        input = noteDto.input,
                        type = noteDto.type.getMaterialType(),
                        userId = noteDto.userId,
                        timeStamp = noteDto.timestamp,
                        languageCode = "en",
                        title = noteDto.title
                    )
                    Log.d("GiangPT", "GiangPT: ${localNoteDetail}")
                    repository.insertStudyMaterial(localNoteDetail)
                }
            }
            repository.insertSummary(summary)
        }
    }

    fun generateStudy(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            // Set loading state to true
            _uiState.update { it.copy(isLoading = true) }

            var noteId: String? = null
            when (uiState.value.inputType) {
                INPUT_TYPE_TEXT -> {
                    if (isValidText()) {
                        val response = repository.createTextNoteSummary(uiState.value.textInput)
                        Log.d("InputViewModel", "Response: $response")
                        if (response is NetworkResult.Success) {
                            noteId = response.data.noteId
                        }
                        _uiState.update { it.copy(isLoading = false, responseResult = response) }
                    }
                }
                INPUT_TYPE_LINK -> {
                    if (isValidLink()) {
                        val response = repository.createLinkNoteSummary(uiState.value.linkInput)
                        Log.d("InputViewModel", "Response: $response")
                        if (response is NetworkResult.Success) {
                            noteId = response.data.noteId
                        }
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
                            if (response is NetworkResult.Success) {
                                noteId = response.data.noteId
                            }
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
                INPUT_TYPE_AUDIO -> {
                    if (uiState.value.audioPath != null) {
                        try {
                            val response =
                                repository.createAudioNoteSummary(uiState.value.audioPath!!)
                            if (response is NetworkResult.Success) {
                                noteId = response.data.noteId
                            }
                            _uiState.update {
                            it.copy(
                                    isLoading = false,
                                    responseResult = response
                                )
                            }
                        } catch (e: Exception) {
                            Log.e("InputViewModel", "Error processing audio", e)
                            _uiState.update {
                            it.copy(
                                    isLoading = false,
                                    responseResult = NetworkResult.Error("Error processing audio: ${e.message}")
                                )
                            }
                        }
                    } else {
                        _uiState.update { it.copy(isLoading = false) }
                    }
                }
                INPUT_TYPE_PHOTO -> {
                    uiState.value.photoUri?.let { uri ->
                        try {
                            val response = repository.createImageNoteSummary(uri.toString())
                            if (response is NetworkResult.Success) {
                                noteId = response.data.noteId
                            }
                            _uiState.update {
                            it.copy(
                                    isLoading = false,
                                    responseResult = response
                                )
                            }
                        } catch (e: Exception) {
                            Log.e("InputViewModel", "Error processing image", e)
                            _uiState.update {
                            it.copy(
                                    isLoading = false,
                                    responseResult = NetworkResult.Error("Error processing image: ${e.message}")
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
    val photoUri: Uri? = null,
    val audioPath: String? = null,
    val isLoading: Boolean = false,
    val responseResult: NetworkResult<SummaryDto>? = null
)