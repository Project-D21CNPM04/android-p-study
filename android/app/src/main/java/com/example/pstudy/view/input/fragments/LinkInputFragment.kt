package com.example.pstudy.view.input.fragments

import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import com.example.pstudy.R
import com.example.pstudy.databinding.FragmentLinkInputBinding

class LinkInputFragment : BaseInputFragment<FragmentLinkInputBinding>() {

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentLinkInputBinding.inflate(inflater, container, false)

    override fun setupViews() {
        binding.etLink.doAfterTextChanged {
            viewModel.updateLink(it.toString())
        }

        binding.btnPaste.setOnClickListener {
            pasteFromClipboard()
        }
    }

    override fun observeViewModel() {
        // No specific observations needed for link fragment
    }

    override fun validateAndSubmit() {
        if (viewModel.isValidLink()) {
            viewModel.generateStudy(requireContext())
        } else {
            showSnackbar(R.string.error_invalid_link)
        }
    }

    private fun pasteFromClipboard() {
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = clipboard.primaryClip
        if (clip != null && clip.itemCount > 0) {
            val text = clip.getItemAt(0).text.toString()
            binding.etLink.setText(text)
            viewModel.updateLink(text)
        }
    }

    companion object {
        fun newInstance() = LinkInputFragment()
    }
}