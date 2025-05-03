package com.example.pstudy.view.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pstudy.data.model.FlashCard
import com.example.pstudy.data.model.StudyMaterials
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ResultViewModel : ViewModel() {

    private val _viewState = MutableStateFlow(ResultViewState())
    val viewState = _viewState.asStateFlow()

    fun loadResultData(studyMaterials: StudyMaterials?) {
        viewModelScope.launch {
            _viewState.update { currentState ->
                val initialFlashCardStates =
                    studyMaterials?.flashCards?.map { Pair(it, true) } ?: emptyList()
                currentState.copy(
                    isLoading = false,
                    resultTitle = studyMaterials?.title ?: "Result",
                    result = studyMaterials,
                    flashCardStates = initialFlashCardStates,
                    currentFlashcardIndex = if (initialFlashCardStates.isNotEmpty()) 0 else -1
                )
            }
        }
    }

    fun navigateToNextFlashcard() {
        _viewState.update { currentState ->
            if (currentState.flashCardStates.isEmpty()) return@update currentState
            val nextIndex =
                (currentState.currentFlashcardIndex + 1) % currentState.flashCardStates.size
            val updatedStates = currentState.flashCardStates.mapIndexed { index, pair ->
                if (index == nextIndex) pair.copy(second = true) else pair
            }
            currentState.copy(currentFlashcardIndex = nextIndex, flashCardStates = updatedStates)
        }
    }

    fun navigateToPreviousFlashcard() {
        _viewState.update { currentState ->
            if (currentState.flashCardStates.isEmpty()) return@update currentState
            val prevIndex =
                (currentState.currentFlashcardIndex - 1 + currentState.flashCardStates.size) % currentState.flashCardStates.size
            val updatedStates = currentState.flashCardStates.mapIndexed { index, pair ->
                if (index == prevIndex) pair.copy(second = true) else pair
            }
            currentState.copy(currentFlashcardIndex = prevIndex, flashCardStates = updatedStates)
        }
    }

    fun flipCurrentFlashcard() {
        _viewState.update { currentState ->
            if (currentState.flashCardStates.isEmpty() || currentState.currentFlashcardIndex == -1) return@update currentState
            val indexToFlip = currentState.currentFlashcardIndex
            val updatedStates = currentState.flashCardStates.toMutableList().apply {
                val currentPair = this[indexToFlip]
                this[indexToFlip] = currentPair.copy(second = !currentPair.second)
            }
            currentState.copy(flashCardStates = updatedStates)
        }
    }
}

data class ResultViewState(
    val isLoading: Boolean = true,
    val resultTitle: String = "Processing Result",
    val result: StudyMaterials? = null,
    val currentTab: Int = 0,
    val flashCardStates: List<Pair<FlashCard, Boolean>> = emptyList(),
    val currentFlashcardIndex: Int = -1
)