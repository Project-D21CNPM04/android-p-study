package com.example.pstudy.view.result.fragment

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.base.ui.base.BindingFragmentLazyPager
import com.example.pstudy.R
import com.example.pstudy.data.model.FlashCard
import com.example.pstudy.databinding.FragmentFlashcardsBinding
import com.example.pstudy.view.result.ResultViewModel
import com.example.pstudy.view.result.ResultViewState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class FlashcardsFragment : BindingFragmentLazyPager<FragmentFlashcardsBinding>() {

    private val viewModel: ResultViewModel by activityViewModels()

    private lateinit var frontAnimOut: AnimatorSet
    private lateinit var backAnimIn: AnimatorSet
    private lateinit var frontAnimIn: AnimatorSet
    private lateinit var backAnimOut: AnimatorSet

    // Track the previous flashcard state for smoother transitions
    private var previousCardIndex = -1
    private var previousCardState = true  // true = showing front

    override fun inflateBinding(inflater: LayoutInflater): FragmentFlashcardsBinding {
        return FragmentFlashcardsBinding.inflate(inflater)
    }

    override fun updateUI() {
        setupAnimations()
        setupClickListeners()
        observeViewState()
    }

    private fun setupAnimations() {
        val scale = requireContext().resources.displayMetrics.density
        binding.cardFront.cameraDistance = 8000 * scale
        binding.cardBack.cameraDistance = 8000 * scale

        frontAnimOut =
            AnimatorInflater.loadAnimator(context, R.animator.flashcard_flip_out) as AnimatorSet
        backAnimIn =
            AnimatorInflater.loadAnimator(context, R.animator.flashcard_flip_in) as AnimatorSet
        frontAnimIn =
            AnimatorInflater.loadAnimator(context, R.animator.flashcard_flip_in) as AnimatorSet
        backAnimOut =
            AnimatorInflater.loadAnimator(context, R.animator.flashcard_flip_out) as AnimatorSet
    }

    private fun observeViewState() {
        viewModel.viewState
            .map {
                Triple(
                    it.flashCardStates.flashCards,
                    it.flashCardStates.currentFlashcardIndex,
                    it.isFlashCardsLoading
                )
            }
            .distinctUntilChanged()
            .onEach { (flashCardStates, currentIndex, isFlashCardsLoading) ->
                if (isFlashCardsLoading) {
                    binding.cardFront.isVisible = false
                    binding.cardBack.isVisible = false
                    binding.progressBar.isVisible = true
                    binding.btnGenerateFlashcards.visibility = View.GONE
                } else {
                    binding.progressBar.isVisible = false
                    updateFlashcardUI(flashCardStates, currentIndex)
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun setupClickListeners() {
        binding.btnGenerateFlashcards.setOnClickListener {
            val studyMaterials = viewModel.viewState.value.result
            if (studyMaterials != null) {
                binding.btnGenerateFlashcards.visibility = View.GONE
                viewModel.generateFlashCards(studyMaterials.id, studyMaterials)
            }
        }

        binding.btnNext.setOnClickListener {
            viewModel.navigateToNextFlashcard()
        }

        binding.btnPrevious.setOnClickListener {
            viewModel.navigateToPreviousFlashcard()
        }

        binding.flashcardContainer.setOnClickListener {
            viewModel.flipCurrentFlashcard()
        }
    }

    private fun updateFlashcardUI(
        flashCardStates: List<Pair<FlashCard, Boolean>>,
        currentIndex: Int
    ) {
        if (flashCardStates.isEmpty() || currentIndex == -1) {
            binding.tvQuestion.text = ""
            binding.tvAnswer.text = ""
            binding.cardFront.isVisible = false
            binding.cardBack.isVisible = false
            binding.btnNext.isVisible = false
            binding.btnPrevious.isVisible = false
            binding.flashcardContainer.isClickable = false
            binding.tvPositionIndicator.text = ""
            binding.tvEmptyState.isVisible = true
            previousCardIndex = -1
            binding.btnGenerateFlashcards.visibility = View.VISIBLE
            return
        }

        binding.tvEmptyState.isVisible = false
        binding.btnGenerateFlashcards.visibility = View.GONE
        binding.btnNext.isVisible = true
        binding.btnPrevious.isVisible = true
        binding.flashcardContainer.isClickable = true

        val (currentCard, isFrontShowing) = flashCardStates[currentIndex]

        binding.tvQuestion.text = currentCard.content.front
        binding.tvAnswer.text = currentCard.content.back
        binding.tvFlipInstruction.isVisible = isFrontShowing

        // Update position indicator
        binding.tvPositionIndicator.text = "${currentIndex + 1}/${flashCardStates.size}"

        // Handle card transition based on navigation (index changed) or card flip
        if (previousCardIndex != currentIndex) {
            // We're navigating to a different card, so set the state directly without animation
            binding.cardFront.isVisible = isFrontShowing
            binding.cardBack.isVisible = !isFrontShowing
            binding.cardFront.alpha = if (isFrontShowing) 1f else 0f
            binding.cardBack.alpha = if (!isFrontShowing) 1f else 0f
            binding.cardFront.rotationY = 0f
            binding.cardBack.rotationY = 0f
            previousCardIndex = currentIndex
            previousCardState = isFrontShowing
        } else if (previousCardState != isFrontShowing) {
            // Same card but state changed - animate the flip
            flipCard(isFrontShowing)
            previousCardState = isFrontShowing
        }
    }

    private fun flipCard(showFront: Boolean) {
        frontAnimOut.cancel()
        backAnimIn.cancel()
        frontAnimIn.cancel()
        backAnimOut.cancel()

        if (showFront) {
            backAnimOut.setTarget(binding.cardBack)
            frontAnimIn.setTarget(binding.cardFront)

            backAnimOut.start()
            frontAnimIn.start()
            binding.cardFront.isVisible = true
            backAnimOut.doOnEnd { binding.cardBack.isVisible = false }

        } else {
            frontAnimOut.setTarget(binding.cardFront)
            backAnimIn.setTarget(binding.cardBack)

            frontAnimOut.start()
            backAnimIn.start()
            binding.cardBack.isVisible = true
            frontAnimOut.doOnEnd { binding.cardFront.isVisible = false }
        }
    }

    companion object {
        fun newInstance() = FlashcardsFragment()
    }
}