package com.example.pstudy.view.result.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.SeekBar
import com.example.base.ui.base.BaseDialogBinding
import com.example.pstudy.R
import com.example.pstudy.databinding.DialogGenerateOptionsBinding
import com.google.android.material.button.MaterialButton

class GenerateOptionsDialog : BaseDialogBinding<DialogGenerateOptionsBinding>() {

    companion object {
        const val TAG = "GenerateOptionsDialog"
        private const val ARG_TITLE = "title"
        private const val ARG_TYPE = "type"

        const val TYPE_FLASHCARDS = "flashcards"
        const val TYPE_QUIZZES = "quizzes"

        const val DIFFICULTY_EASY = 1
        const val DIFFICULTY_MEDIUM = 2
        const val DIFFICULTY_HARD = 3
        const val DIFFICULTY_MIXED = 4

        fun newInstance(title: String, type: String): GenerateOptionsDialog {
            val args = Bundle().apply {
                putString(ARG_TITLE, title)
                putString(ARG_TYPE, type)
            }
            return GenerateOptionsDialog().apply {
                arguments = args
            }
        }
    }

    interface GenerateOptionsListener {
        fun onGenerateOptionsConfirmed(count: Int, difficulty: Int, type: String)
    }

    private var listener: GenerateOptionsListener? = null

    fun setOptionsListener(listener: GenerateOptionsListener) {
        this.listener = listener
    }

    override fun getLayoutBinding(inflater: LayoutInflater): DialogGenerateOptionsBinding {
        return DialogGenerateOptionsBinding.inflate(inflater)
    }

    // Keep track of selected difficulty button
    private var selectedDifficulty = "Medium"
    private var selectedButton: MaterialButton? = null

    override fun updateUI(savedInstanceState: Bundle?) {
        val title = arguments?.getString(ARG_TITLE) ?: "Set Generation Options"
        val type = arguments?.getString(ARG_TYPE) ?: TYPE_FLASHCARDS

        setupUI(type, title)
        setupButtons()
    }

    private fun setupButtons() {
        binding.btnConfirm.setOnClickListener {
            val count = binding.countSeekBar.progress + 5 // Min is 5
            val difficulty = when (selectedDifficulty) {
                "Easy" -> DIFFICULTY_EASY
                "Medium" -> DIFFICULTY_MEDIUM
                "Hard" -> DIFFICULTY_HARD
                "Mixed" -> DIFFICULTY_MIXED
                else -> DIFFICULTY_MEDIUM
            }

            listener?.onGenerateOptionsConfirmed(
                count,
                difficulty,
                arguments?.getString(ARG_TYPE) ?: TYPE_FLASHCARDS
            )
            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun setupUI(type: String, title: String) {
        // Set dialog title
        binding.dialogTitle.text = title

        // Setup count seek bar (5-30)
        binding.countSeekBar.max = 25 // 30 - 5
        binding.countSeekBar.progress = 0 // Default to 5
        updateCountText(5)
        
        binding.countSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val actualCount = progress + 5 // Min is 5
                updateCountText(actualCount)
            }
            
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Set up difficulty buttons click handlers
        setupDifficultyButtons()
        
        // Set item name based on type
        if (type == TYPE_FLASHCARDS) {
            binding.tvCountLabel.text = "Number of Flashcards"
        } else {
            binding.tvCountLabel.text = "Number of Quizzes"
        }
    }

    private fun setupDifficultyButtons() {
        // Define all buttons
        val buttons = listOf(
            binding.btnEasy,
            binding.btnMedium,
            binding.btnHard,
            binding.btnMixed
        )

        // Set the default selected button
        selectButton(binding.btnMedium)

        // Set click listeners for all buttons
        buttons.forEach { button ->
            button.setOnClickListener {
                selectButton(button)
            }
        }
    }

    private fun selectButton(button: MaterialButton) {
        // Deselect the previous button if any
        selectedButton?.let {
            it.strokeWidth = 1
            it.backgroundTintList = null
            it.setTextColor(resources.getColor(android.R.color.black, null))
        }

        // Select the new button
        button.strokeWidth = 2
        button.backgroundTintList = resources.getColorStateList(R.color.color_primary, null)
        button.setTextColor(resources.getColor(android.R.color.white, null))
        selectedButton = button
        selectedDifficulty = button.text.toString()
    }
    
    private fun updateCountText(count: Int) {
        binding.tvCountValue.text = count.toString()
    }

    override fun isFullWidth(): Boolean = true
}