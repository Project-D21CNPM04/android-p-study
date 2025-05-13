package com.example.pstudy.view.result.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.base.ui.base.BindingFragmentLazyPager
import com.example.pstudy.R
import com.example.pstudy.data.model.Quiz
import com.example.pstudy.databinding.FragmentQuizzesBinding
import com.example.pstudy.view.result.ResultViewModel
import com.example.pstudy.view.result.dialog.GenerateOptionsDialog
import com.google.android.material.card.MaterialCardView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class QuizzesFragment : BindingFragmentLazyPager<FragmentQuizzesBinding>() {

    private val viewModel: ResultViewModel by activityViewModels()

    override fun inflateBinding(inflater: LayoutInflater): FragmentQuizzesBinding {
        return FragmentQuizzesBinding.inflate(inflater)
    }

    override fun updateUI() {
        setupClickListeners()
        observeViewState()
    }

    private fun setupClickListeners() {
        binding.btnGenerateQuizzes.setOnClickListener {
            val studyMaterials = viewModel.viewState.value.result
            if (studyMaterials != null) {
                showGenerateOptionsDialog(studyMaterials.id)
            }
        }

        binding.btnNext.setOnClickListener {
            val quizzes = viewModel.viewState.value.quizzesState.quizzes
            val currentIndex = viewModel.viewState.value.quizzesState.currentQuizIndex
            if (quizzes.isNotEmpty() && currentIndex != -1) {
                val nextIndex = (currentIndex + 1) % quizzes.size
                viewModel.navigateToQuiz(nextIndex)
            }
        }

        binding.btnPrevious.setOnClickListener {
            val quizzes = viewModel.viewState.value.quizzesState.quizzes
            val currentIndex = viewModel.viewState.value.quizzesState.currentQuizIndex
            if (quizzes.isNotEmpty() && currentIndex != -1) {
                val prevIndex = (currentIndex - 1 + quizzes.size) % quizzes.size
                viewModel.navigateToQuiz(prevIndex)
            }
        }

        binding.optionCard1.setOnClickListener { answerQuiz(0) }
        binding.optionCard2.setOnClickListener { answerQuiz(1) }
        binding.optionCard3.setOnClickListener { answerQuiz(2) }
        binding.optionCard4.setOnClickListener { answerQuiz(3) }
    }

    private fun answerQuiz(selectedOption: Int) {
        val currentQuiz = getCurrentQuiz() ?: return

        // Don't allow changing answer if already answered
        if (viewModel.viewState.value.quizzesState.quizzes.isNotEmpty() &&
            viewModel.viewState.value.quizzesState.currentQuizIndex >= 0 &&
            viewModel.viewState.value.quizzesState.quizzes[viewModel.viewState.value.quizzesState.currentQuizIndex].second
        ) {
            return
        }

        val isCorrect = selectedOption < currentQuiz.choices.size &&
                currentQuiz.choices[selectedOption] == currentQuiz.answer
        viewModel.answerQuiz(selectedOption)
        showFeedback(selectedOption, isCorrect)
    }

    private fun getCurrentQuiz(): Quiz? {
        val state = viewModel.viewState.value
        if (state.quizzesState.quizzes.isNotEmpty() && state.quizzesState.currentQuizIndex != -1) {
            return state.quizzesState.quizzes[state.quizzesState.currentQuizIndex].first
        }
        return null
    }

    private fun getOptionCard(index: Int): MaterialCardView? {
        return when (index) {
            0 -> binding.optionCard1
            1 -> binding.optionCard2
            2 -> binding.optionCard3
            3 -> binding.optionCard4
            else -> null
        }
    }

    private fun showFeedback(selectedOption: Int, isCorrect: Boolean) {
        // Apply visual feedback on the selected option
        val selectedCard = getOptionCard(selectedOption)

        // Visual feedback for correct/incorrect answers
        if (isCorrect) {
            selectedCard?.apply {
                setCardBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.correct_answer)
                )
                strokeWidth = resources.getDimensionPixelSize(R.dimen.card_stroke_width_selected)
                strokeColor = ContextCompat.getColor(requireContext(), R.color.color_primary)
            }
        } else {
            selectedCard?.apply {
                setCardBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.incorrect_answer)
                )
                strokeWidth = resources.getDimensionPixelSize(R.dimen.card_stroke_width_selected)
                strokeColor = ContextCompat.getColor(requireContext(), R.color.color_primary)
            }

            // Show the correct answer
            val currentQuiz = getCurrentQuiz() ?: return
            val correctOptionIndex = currentQuiz.choices.indexOf(currentQuiz.answer)
            val correctCard = getOptionCard(correctOptionIndex)
            correctCard?.apply {
                setCardBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.correct_answer)
                )
                strokeWidth = resources.getDimensionPixelSize(R.dimen.card_stroke_width_selected)
                strokeColor = ContextCompat.getColor(requireContext(), R.color.color_primary)
            }
        }
    }

    private fun observeViewState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.viewState
                .map {
                    Triple(
                        it.quizzesState.quizzes,
                        it.quizzesState.currentQuizIndex,
                        it.isQuizzesLoading
                    )
                }
                .distinctUntilChanged()
                .collect { (quizzes, currentIndex, isQuizzesLoading) ->
                    if (isQuizzesLoading) {
                        binding.progressBar.isVisible = true
                        binding.tvEmptyState.isVisible = false
                        binding.tvQuizNumber.isVisible = false
                        binding.cardQuizQuestion.isVisible = false
                        binding.quizOptionsContainer.isVisible = false
                        binding.btnGenerateQuizzes.visibility = View.GONE
                        binding.navigationContainer.visibility = View.GONE
                    } else {
                        binding.progressBar.isVisible = false

                        if (quizzes.isEmpty()) {
                            binding.tvQuizNumber.isVisible = false
                            binding.cardQuizQuestion.isVisible = false
                            binding.quizOptionsContainer.isVisible = false
                            binding.btnGenerateQuizzes.visibility = View.VISIBLE
                            binding.navigationContainer.visibility = View.GONE
                        } else {
                            binding.btnGenerateQuizzes.visibility = View.GONE
                            updateQuizUI(quizzes, currentIndex)
                        }
                    }
                }
        }
    }

    private fun updateQuizUI(quizzes: List<Pair<Quiz, Boolean>>, currentIndex: Int) {
        if (quizzes.isEmpty() || currentIndex == -1) {
            binding.tvQuizNumber.isVisible = false
            binding.cardQuizQuestion.isVisible = false
            binding.quizOptionsContainer.isVisible = false
            binding.btnGenerateQuizzes.visibility = View.VISIBLE
            binding.navigationContainer.visibility = View.GONE
            return
        }

        binding.tvEmptyState.isVisible = false
        binding.btnGenerateQuizzes.visibility = View.GONE
        binding.tvQuizNumber.isVisible = true
        binding.cardQuizQuestion.isVisible = true
        binding.quizOptionsContainer.isVisible = true
        binding.navigationContainer.visibility = View.VISIBLE

        val currentQuiz = quizzes[currentIndex]
        val quiz = currentQuiz.first
        val isAnswered = currentQuiz.second

        binding.tvQuizNumber.text = "Quiz ${currentIndex + 1} / ${quizzes.size}"
        binding.tvQuizQuestion.text = quiz.questions

        // Reset card backgrounds and strokes
        val bgColor = ContextCompat.getColor(requireContext(), R.color.bg_option_card)
        binding.optionCard1.apply {
            setCardBackgroundColor(bgColor)
            strokeWidth = resources.getDimensionPixelSize(R.dimen.card_stroke_width_normal)
            strokeColor = ContextCompat.getColor(requireContext(), R.color.gray_medium)
        }
        binding.optionCard2.apply {
            setCardBackgroundColor(bgColor)
            strokeWidth = resources.getDimensionPixelSize(R.dimen.card_stroke_width_normal)
            strokeColor = ContextCompat.getColor(requireContext(), R.color.gray_medium)
        }
        binding.optionCard3.apply {
            setCardBackgroundColor(bgColor)
            strokeWidth = resources.getDimensionPixelSize(R.dimen.card_stroke_width_normal)
            strokeColor = ContextCompat.getColor(requireContext(), R.color.gray_medium)
        }
        binding.optionCard4.apply {
            setCardBackgroundColor(bgColor)
            strokeWidth = resources.getDimensionPixelSize(R.dimen.card_stroke_width_normal)
            strokeColor = ContextCompat.getColor(requireContext(), R.color.gray_medium)
        }

        // Set options text
        binding.tvOption1.text = if (quiz.choices.size >= 1) quiz.choices[0] else ""
        binding.tvOption2.text = if (quiz.choices.size >= 2) quiz.choices[1] else ""
        binding.tvOption3.text = if (quiz.choices.size >= 3) quiz.choices[2] else ""
        binding.tvOption4.text = if (quiz.choices.size >= 4) quiz.choices[3] else ""

        // Show/hide options based on available choices
        binding.optionCard1.isVisible = quiz.choices.size >= 1
        binding.optionCard2.isVisible = quiz.choices.size >= 2
        binding.optionCard3.isVisible = quiz.choices.size >= 3
        binding.optionCard4.isVisible = quiz.choices.size >= 4

        // If already answered, show the feedback
        if (isAnswered) {
            val correctOptionIndex = quiz.choices.indexOf(quiz.answer)
            val correctCard = getOptionCard(correctOptionIndex)
            correctCard?.apply {
                setCardBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.correct_answer)
                )
                strokeWidth = resources.getDimensionPixelSize(R.dimen.card_stroke_width_selected)
                strokeColor = ContextCompat.getColor(requireContext(), R.color.color_primary)
            }

            // If there was a wrong selected answer, show it too
            val selectedAnswerIndex =
                viewModel.viewState.value.quizzesState.selectedAnswerIndexes[currentIndex]
            if (selectedAnswerIndex != null && selectedAnswerIndex != correctOptionIndex) {
                val selectedCard = getOptionCard(selectedAnswerIndex)
                selectedCard?.apply {
                    setCardBackgroundColor(
                        ContextCompat.getColor(requireContext(), R.color.incorrect_answer)
                    )
                    strokeWidth =
                        resources.getDimensionPixelSize(R.dimen.card_stroke_width_selected)
                    strokeColor = ContextCompat.getColor(requireContext(), R.color.color_primary)
                }
            }
        }

        // All navigation buttons are always enabled with circular navigation
        binding.btnNext.isEnabled = true
        binding.btnPrevious.isEnabled = true
    }

    private fun showGenerateOptionsDialog(noteId: String) {
        val dialog = GenerateOptionsDialog.newInstance(
            "Generate Quizzes",
            GenerateOptionsDialog.TYPE_QUIZZES
        )

        dialog.setOptionsListener(object : GenerateOptionsDialog.GenerateOptionsListener {
            override fun onGenerateOptionsConfirmed(count: Int, difficulty: Int, type: String) {
                val studyMaterials = viewModel.viewState.value.result
                if (studyMaterials != null) {
                    binding.btnGenerateQuizzes.visibility = View.GONE
                    binding.progressBar.isVisible = true
                    binding.tvEmptyState.isVisible = false
                    viewModel.generateQuizzes(noteId, studyMaterials, count, difficulty)
                }
            }
        })

        dialog.show(childFragmentManager)
    }

    companion object {
        fun newInstance() = QuizzesFragment()
    }
}