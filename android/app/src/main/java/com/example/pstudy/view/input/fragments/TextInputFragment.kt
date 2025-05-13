package com.example.pstudy.view.input.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import com.example.pstudy.R
import com.example.pstudy.databinding.FragmentTextInputBinding

class TextInputFragment : BaseInputFragment<FragmentTextInputBinding>() {

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentTextInputBinding.inflate(inflater, container, false)

    override fun setupViews() {
        binding.tvCharCount.text = getString(
            R.string.char_count_format,
            0
        )

        binding.etText.doAfterTextChanged {
            val text = it.toString()
            viewModel.updateText(text)
            binding.tvCharCount.text = getString(
                R.string.char_count_format,
                text.length
            )
        }
    }

    override fun observeViewModel() {
        // No specific observations needed for text fragment
    }

    override fun validateAndSubmit() {
        if (viewModel.isValidText()) {
            viewModel.generateStudy(requireContext())
        } else {
            showSnackbar(R.string.error_invalid_text)
        }
    }

    companion object {
        fun newInstance() = TextInputFragment()
    }
}