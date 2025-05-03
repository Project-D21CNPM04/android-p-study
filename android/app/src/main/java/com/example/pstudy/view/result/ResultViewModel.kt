package com.example.pstudy.view.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pstudy.data.model.FlashCard
import com.example.pstudy.data.model.Quiz
import com.example.pstudy.data.model.StudyMaterials
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ResultViewModel : ViewModel() {

    private val _viewState = MutableStateFlow(ResultViewState())
    val viewState = _viewState.asStateFlow()

    fun loadResultData(studyMaterials: StudyMaterials?) {
        viewModelScope.launch {
            _viewState.update { currentState ->
                val flashCardStates =
                    studyMaterials?.flashCards?.map { Pair(it, true) } ?: emptyList()
                val quizStates = studyMaterials?.quizzes?.map { Pair(it, false) } ?: emptyList()

                currentState.copy(
                    isLoading = false,
                    resultTitle = studyMaterials?.input ?: "Result",
                    result = studyMaterials,
                    flashCardStates = FlashCardState(
                        flashCards = flashCardStates,
                        currentFlashcardIndex = if (flashCardStates.isNotEmpty()) 0 else -1
                    ),
                    quizzesState = QuizzesState(
                        quizzes = quizStates,
                        currentQuizIndex = if (quizStates.isNotEmpty()) 0 else -1,
                        selectedAnswerIndexes = emptyMap()
                    )
                )
            }
        }
    }

    fun navigateToNextFlashcard() {
        _viewState.update { currentState ->
            if (currentState.flashCardStates.flashCards.isEmpty()) return@update currentState
            val nextIndex =
                (currentState.flashCardStates.currentFlashcardIndex + 1) % currentState.flashCardStates.flashCards.size
            currentState.copy(
                flashCardStates = currentState.flashCardStates.copy(
                    currentFlashcardIndex = nextIndex
                )
            )
        }
    }

    fun navigateToPreviousFlashcard() {
        _viewState.update { currentState ->
            if (currentState.flashCardStates.flashCards.isEmpty()) return@update currentState
            val prevIndex =
                (currentState.flashCardStates.currentFlashcardIndex - 1 + currentState.flashCardStates.flashCards.size) % currentState.flashCardStates.flashCards.size
            currentState.copy(
                flashCardStates = currentState.flashCardStates.copy(
                    currentFlashcardIndex = prevIndex
                )
            )
        }
    }

    fun flipCurrentFlashcard() {
        _viewState.update { currentState ->
            if (currentState.flashCardStates.flashCards.isEmpty() ||
                currentState.flashCardStates.currentFlashcardIndex == -1
            ) return@update currentState

            val indexToFlip = currentState.flashCardStates.currentFlashcardIndex
            val updatedStates = currentState.flashCardStates.flashCards.toMutableList().apply {
                val currentPair = this[indexToFlip]
                this[indexToFlip] = currentPair.copy(second = !currentPair.second)
            }

            currentState.copy(
                flashCardStates = currentState.flashCardStates.copy(
                    flashCards = updatedStates
                )
            )
        }
    }

    fun navigateToNextQuiz() {
        _viewState.update { currentState ->
            if (currentState.quizzesState.quizzes.isEmpty()) return@update currentState
            val nextIndex =
                (currentState.quizzesState.currentQuizIndex + 1) % currentState.quizzesState.quizzes.size

            currentState.copy(
                quizzesState = currentState.quizzesState.copy(
                    currentQuizIndex = nextIndex,
                    selectedAnswerIndexes = currentState.quizzesState.selectedAnswerIndexes.filter { it.key != nextIndex }
                )
            )
        }
    }

    fun navigateToPreviousQuiz() {
        _viewState.update { currentState ->
            if (currentState.quizzesState.quizzes.isEmpty()) return@update currentState
            val prevIndex =
                (currentState.quizzesState.currentQuizIndex - 1 + currentState.quizzesState.quizzes.size) % currentState.quizzesState.quizzes.size

            currentState.copy(
                quizzesState = currentState.quizzesState.copy(
                    currentQuizIndex = prevIndex,
                    selectedAnswerIndexes = currentState.quizzesState.selectedAnswerIndexes.filter { it.key != prevIndex }
                )
            )
        }
    }

    fun answerQuiz(answerIndex: Int) {
        _viewState.update { currentState ->
            if (currentState.quizzesState.quizzes.isEmpty() ||
                currentState.quizzesState.currentQuizIndex == -1
            ) return@update currentState

            val indexToUpdate = currentState.quizzesState.currentQuizIndex
            val updatedStates = currentState.quizzesState.quizzes.toMutableList().apply {
                val currentPair = this[indexToUpdate]
                this[indexToUpdate] = currentPair.copy(second = true)
            }

            currentState.copy(
                quizzesState = currentState.quizzesState.copy(
                    quizzes = updatedStates,
                    selectedAnswerIndexes = currentState.quizzesState.selectedAnswerIndexes + (indexToUpdate to answerIndex)
                )
            )
        }
    }
}

data class ResultViewState(
    val isLoading: Boolean = true,
    val resultTitle: String = "Processing Result",
    val result: StudyMaterials? = null,
    val currentTab: Int = 0,
    val flashCardStates: FlashCardState = FlashCardState(),
    val quizzesState: QuizzesState = QuizzesState()
)

data class FlashCardState(
    val flashCards: List<Pair<FlashCard, Boolean>> = emptyList(),
    val currentFlashcardIndex: Int = -1
)

data class QuizzesState(
    val quizzes: List<Pair<Quiz, Boolean>> = emptyList(),
    val currentQuizIndex: Int = -1,
    val selectedAnswerIndexes: Map<Int, Int> = emptyMap()
)