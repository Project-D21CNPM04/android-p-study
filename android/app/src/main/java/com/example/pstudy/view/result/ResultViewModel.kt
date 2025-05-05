package com.example.pstudy.view.result

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pstudy.data.model.Content
import com.example.pstudy.data.model.FlashCard
import com.example.pstudy.data.model.MindMap
import com.example.pstudy.data.model.Quiz
import com.example.pstudy.data.model.StudyMaterials
import com.example.pstudy.data.model.Summary
import com.example.pstudy.data.remote.utils.NetworkResult
import com.example.pstudy.data.repository.StudyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val studyRepository: StudyRepository
) : ViewModel() {

    private val _viewState = MutableStateFlow(ResultViewState())
    val viewState = _viewState.asStateFlow()

    fun loadResultData(studyMaterials: StudyMaterials?) {
        viewModelScope.launch(Dispatchers.IO) {
            // Initialize with basic data first
            _viewState.update { currentState ->

                currentState.copy(
                    isLoading = false,
                    resultTitle = studyMaterials?.input ?: "Result",
                    result = studyMaterials?.copy(),
                )
            }

            // Check for missing components and generate them if needed
            if (studyMaterials != null) {
                val noteId = studyMaterials.id
                generateSummary(noteId, studyMaterials)
                generateMindMap(noteId, studyMaterials)
                generateFlashCards(noteId, studyMaterials)
                generateQuizzes(noteId, studyMaterials)
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

    private fun generateSummary(noteId: String, studyMaterials: StudyMaterials) {
        viewModelScope.launch {
            _viewState.update { it.copy(isSummaryLoading = true) }

            try {
                // First try to get from local storage
                val localSummary = studyRepository.getSummary(noteId)

                if (localSummary != null) {

                    _viewState.update {
                        it.copy(
                            isSummaryLoading = false,
                            summary = localSummary,
                        )
                    }
                } else {
                    // If not available locally, generate from API
                    val text = studyMaterials.input
                    val result = studyRepository.createTextNoteSummary(text)

                    if (result is NetworkResult.Success) {
                        val summary = Summary(
                            id = result.data.id,
                            noteId = noteId,
                            content = result.data.content
                        )

                        _viewState.update {
                            it.copy(
                                isSummaryLoading = false,
                                summary = summary,
                            )
                        }

                        // Save summary to local storage
                        studyRepository.insertSummary(summary)
                    } else {
                        _viewState.update { it.copy(isSummaryLoading = false) }
                    }
                }
            } catch (e: Exception) {
                _viewState.update { it.copy(isSummaryLoading = false) }
            }
        }
    }

    private fun generateMindMap(noteId: String, studyMaterials: StudyMaterials) {
        viewModelScope.launch {
            _viewState.update { it.copy(isMindMapLoading = true) }

            try {
                // First try to get from local storage
                val localMindMap = studyRepository.getMindMap(noteId)

                if (localMindMap != null) {
                    _viewState.update {
                        it.copy(
                            isMindMapLoading = false,
                            mindMap = localMindMap,
                        )
                    }
                } else {
                    // If not available locally, generate from API
                    val result = studyRepository.generateMindMap(noteId)

                    if (result is NetworkResult.Success) {
                        val mindMap = MindMap(
                            id = result.data.id,
                            content = result.data.content,
                            summary = result.data.summary
                        )

                        _viewState.update {
                            it.copy(
                                isMindMapLoading = false,
                                mindMap = mindMap,
                            )
                        }

                        // Save mind map to local storage
                        studyRepository.createMindMap(mindMap, studyMaterials.id)
                    } else {
                        _viewState.update { it.copy(isMindMapLoading = false) }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _viewState.update { it.copy(isMindMapLoading = false) }
            }
        }
    }

    private fun generateFlashCards(noteId: String, studyMaterials: StudyMaterials) {
        viewModelScope.launch {
            _viewState.update { it.copy(isFlashCardsLoading = true) }

            try {
                // First try to get from local storage
                val localFlashCards = studyRepository.getFlashCards(noteId)

                if (localFlashCards.isNotEmpty()) {
                    // Use the local flashcards if available
                    // Create flashcard states with default front showing (true)
                    val flashCardStates = localFlashCards.map { Pair(it, true) }

                    _viewState.update { currentState ->
                        currentState.copy(
                            isFlashCardsLoading = false,
                            flashCardStates = FlashCardState(
                                flashCards = flashCardStates,
                                currentFlashcardIndex = if (flashCardStates.isNotEmpty()) 0 else -1
                            ),
                        )
                    }
                } else {
                    // If not available locally, generate from API
                    val result = studyRepository.generateFlashCards(noteId)

                    Log.d("StudyViewModel", "FlashCards result: $result")

                    if (result is NetworkResult.Success) {
                        val flashCards = result.data.map { dto ->
                            FlashCard(
                                id = dto.id,
                                title = dto.title,
                                content = Content(
                                    front = dto.content.front,
                                    back = dto.content.back
                                )
                            )
                        }

                        // Create flashcard states with default front showing (true)
                        val flashCardStates = flashCards.map { Pair(it, true) }

                        Log.d("StudyViewModel", "FlashCards: $flashCardStates")

                        _viewState.update { currentState ->
                            currentState.copy(
                                isFlashCardsLoading = false,
                                flashCardStates = FlashCardState(
                                    flashCards = flashCardStates,
                                    currentFlashcardIndex = if (flashCardStates.isNotEmpty()) 0 else -1
                                ),
                            )
                        }

                        // Save flashcards to local storage
                        studyRepository.createFlashCards(flashCards, studyMaterials.id)
                    } else {
                        _viewState.update { it.copy(isFlashCardsLoading = false) }
                    }
                }
            } catch (e: Exception) {
                _viewState.update { it.copy(isFlashCardsLoading = false) }
            }
        }
    }

    private fun generateQuizzes(noteId: String, studyMaterials: StudyMaterials) {
        viewModelScope.launch {
            _viewState.update { it.copy(isQuizzesLoading = true) }

            try {
                // First try to get from local storage
                val localQuizzes = studyRepository.getQuizzes(noteId)

                if (localQuizzes.isNotEmpty()) {
                    // Use the local quizzes if available
                    // Create quiz states with default unanswered state (false)
                    val quizStates = localQuizzes.map { Pair(it, false) }

                    _viewState.update { currentState ->
                        currentState.copy(
                            isQuizzesLoading = false,
                            quizzesState = QuizzesState(
                                quizzes = quizStates,
                                currentQuizIndex = if (quizStates.isNotEmpty()) 0 else -1,
                                selectedAnswerIndexes = emptyMap()
                            ),
                        )
                    }
                } else {
                    // If not available locally, generate from API
                    val result = studyRepository.generateQuiz(noteId)

                    if (result is NetworkResult.Success) {
                        val quizzes = result.data.map { dto ->
                            Quiz(
                                id = dto.id,
                                questions = dto.question,
                                choices = dto.choices,
                                answer = dto.answer
                            )
                        }

                        // Create quiz states with default unanswered state (false)
                        val quizStates = quizzes.map { Pair(it, false) }

                        _viewState.update { currentState ->
                            currentState.copy(
                                isQuizzesLoading = false,
                                quizzesState = QuizzesState(
                                    quizzes = quizStates,
                                    currentQuizIndex = if (quizStates.isNotEmpty()) 0 else -1,
                                    selectedAnswerIndexes = emptyMap()
                                ),
                            )
                        }

                        // Save quizzes to local storage
                        studyRepository.createQuizzes(quizzes, studyMaterials.id)
                    } else {
                        _viewState.update { it.copy(isQuizzesLoading = false) }
                    }
                }
            } catch (e: Exception) {
                _viewState.update { it.copy(isQuizzesLoading = false) }
            }
        }
    }
}

data class ResultViewState(
    val isLoading: Boolean = true,
    val isSummaryLoading: Boolean = false,
    val isMindMapLoading: Boolean = false,
    val isFlashCardsLoading: Boolean = false,
    val isQuizzesLoading: Boolean = false,
    val resultTitle: String = "Processing Result",
    val result: StudyMaterials? = null,
    val currentTab: Int = 0,
    val summary: Summary? = null,
    val mindMap: MindMap? = null,
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