package com.example.pstudy.view.input

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.pstudy.view.input.InputActivity.Companion.INPUT_TYPE_TEXT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.regex.Pattern

@HiltViewModel
class InputViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(InputUiState())
    val uiState = _uiState.asStateFlow()

    val currentInputType get() = _uiState.value.inputType
    val currentLink get() = _uiState.value.linkInput
    val currentText get() = _uiState.value.textInput
    val selectedFileUri get() = _uiState.value.fileUri

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
}

data class InputUiState(
    val inputType: String = INPUT_TYPE_TEXT,
    val linkInput: String = "",
    val textInput: String = "",
    val fileUri: Uri? = null
)