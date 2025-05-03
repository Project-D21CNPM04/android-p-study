package com.example.pstudy.view.result.fragment

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.base.ui.base.BindingFragmentLazyPager
import com.example.pstudy.R
import com.example.pstudy.data.model.FlashCard
import com.example.pstudy.databinding.FragmentFlashcardsBinding
import com.example.pstudy.view.result.ResultViewModel
import com.example.pstudy.view.result.ResultViewState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FlashcardsFragment : BindingFragmentLazyPager<FragmentFlashcardsBinding>() {

    private val viewModel: ResultViewModel by activityViewModels()

    private lateinit var frontAnimOut: AnimatorSet
    private lateinit var backAnimIn: AnimatorSet
    private lateinit var frontAnimIn: AnimatorSet
    private lateinit var backAnimOut: AnimatorSet

    override fun inflateBinding(inflater: LayoutInflater): FragmentFlashcardsBinding {
        return FragmentFlashcardsBinding.inflate(inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAnimations()
        setupClickListeners()
        observeViewState()
    }

    override fun updateUI() {
        // println("FlashcardsFragment updateUI called")
        // Re-apply state if needed, though observeViewState should handle most cases
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
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewState
                    .map { Triple(it.flashCardStates, it.currentFlashcardIndex, it.isLoading) }
                    .distinctUntilChanged()
                    .collect { (flashCardStates, currentIndex, isLoading) ->
                        if (isLoading) {
                            binding.cardFront.isVisible = false
                            binding.cardBack.isVisible = false
                            binding.tvQuestionNumber.isVisible = false
                            // TODO: Show loading indicator
                        } else {
                            binding.tvQuestionNumber.isVisible = true
                            // TODO: Hide loading indicator
                            updateFlashcardUI(flashCardStates, currentIndex)
                        }
                    }
            }
        }
    }

    private fun setupClickListeners() {
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
            binding.tvQuestionNumber.text = ""
            binding.tvQuestion.text = ""
            binding.tvAnswer.text = ""
            binding.cardFront.isVisible = false
            binding.cardBack.isVisible = false
            binding.btnNext.isEnabled = false
            binding.btnPrevious.isEnabled = false
            binding.flashcardContainer.isClickable = false
            return
        }

        binding.btnNext.isEnabled = true
        binding.btnPrevious.isEnabled = true
        binding.flashcardContainer.isClickable = true

        val (currentCard, isFrontShowing) = flashCardStates[currentIndex]

        binding.tvQuestionNumber.text = getString(
            R.string.flashcard_question_label,
            currentIndex + 1,
            flashCardStates.size
        )
        binding.tvQuestion.text = currentCard.front
        binding.tvAnswer.text = currentCard.back
        binding.tvFlipInstruction.isVisible = isFrontShowing

        val currentlyVisibleFront = binding.cardFront.isVisible && binding.cardFront.alpha == 1f

        if (isFrontShowing != currentlyVisibleFront) {
            flipCard(isFrontShowing)
        } else {
            binding.cardFront.isVisible = isFrontShowing
            binding.cardBack.isVisible = !isFrontShowing
            binding.cardFront.alpha = if (isFrontShowing) 1f else 0f
            binding.cardBack.alpha = if (!isFrontShowing) 1f else 0f
            binding.cardFront.rotationY = 0f
            binding.cardBack.rotationY = 0f
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