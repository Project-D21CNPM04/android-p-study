package com.example.pstudy.view.input.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import com.example.pstudy.R
import com.example.pstudy.databinding.FragmentFileInputBinding

class FileInputFragment : BaseInputFragment<FragmentFileInputBinding>() {

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            if (viewModel.isValidFile(requireContext(), it)) {
                viewModel.setSelectedFile(it)
                binding.tvSelectedFile.text = getFileNameFromUri(it)
                binding.tvNoFileSelected.visibility = View.GONE
                binding.fileInfoCard.visibility = View.VISIBLE
            } else {
                showSnackbar(R.string.error_invalid_file)
            }
        }
    }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentFileInputBinding.inflate(inflater, container, false)

    override fun setupViews() {
        binding.btnSelectFile.setOnClickListener {
            selectFile()
        }
    }

    override fun observeViewModel() {
        // No specific observations needed for file fragment
    }

    override fun validateAndSubmit() {
        if (viewModel.selectedFileUri != null) {
            if (viewModel.isValidFile(requireContext(), viewModel.selectedFileUri)) {
                viewModel.generateStudy(requireContext())
            } else {
                showSnackbar(R.string.error_invalid_file)
            }
        } else {
            showSnackbar(R.string.error_no_file_selected)
        }
    }

    private fun selectFile() {
        getContent.launch("application/pdf")
    }

    private fun getFileNameFromUri(uri: Uri): String {
        val contentResolver = requireContext().contentResolver
        val cursor = contentResolver.query(uri, null, null, null, null)

        return cursor?.use {
            val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            it.moveToFirst()
            if (nameIndex != -1) it.getString(nameIndex) else uri.lastPathSegment ?: "Unknown file"
        } ?: uri.lastPathSegment ?: "Unknown file"
    }

    companion object {
        fun newInstance() = FileInputFragment()
    }
}