package com.example.pstudy.view.input

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.example.base.ui.base.BindingActivity
import com.example.pstudy.R
import com.example.pstudy.data.firebase.FirebaseAuthHelper
import com.example.pstudy.data.mapper.toDomain
import com.example.pstudy.data.model.MaterialType
import com.example.pstudy.data.model.StudyMaterials
import com.example.pstudy.data.remote.utils.NetworkResult
import com.example.pstudy.databinding.ActivityInputBinding
import com.example.pstudy.ext.getMaterialType
import com.example.pstudy.view.result.ResultActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class InputActivity : BindingActivity<ActivityInputBinding>() {
    override fun inflateBinding(layoutInflater: LayoutInflater) =
        ActivityInputBinding.inflate(layoutInflater)

    private val viewModel by viewModels<InputViewModel>()

    override fun getStatusBarColor() = R.color.black

    override fun updateUI(savedInstanceState: Bundle?) {
        parseIntent()
        initView()
        observeViewModel()
        handleOnClick()
    }

    private fun parseIntent() {
        intent.getStringExtra(ARG_INPUT_TYPE)?.let {
            viewModel.setInputType(it)
        } ?: finish()
    }

    private fun initView() {
        when (viewModel.currentInputType) {
            INPUT_TYPE_FILE -> {
                binding.fileInputLayout.visibility = View.VISIBLE
                binding.linkInputLayout.visibility = View.GONE
                binding.textInputLayout.visibility = View.GONE

                binding.tvTitle.text = getString(R.string.input_file_title)
                binding.btnSubmit.text = getString(R.string.input_file_submit)
            }

            INPUT_TYPE_LINK -> {
                binding.fileInputLayout.visibility = View.GONE
                binding.linkInputLayout.visibility = View.VISIBLE
                binding.textInputLayout.visibility = View.GONE

                binding.tvTitle.text = getString(R.string.input_link_title)
                binding.btnSubmit.text = getString(R.string.input_link_submit)

                binding.etLink.doAfterTextChanged {
                    viewModel.updateLink(it.toString())
                }
            }

            INPUT_TYPE_TEXT -> {
                binding.fileInputLayout.visibility = View.GONE
                binding.linkInputLayout.visibility = View.GONE
                binding.textInputLayout.visibility = View.VISIBLE

                binding.tvTitle.text = getString(R.string.input_text_title)
                binding.btnSubmit.text = getString(R.string.input_text_submit)

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

            else -> finish()
        }
    }

    private fun observeViewModel() {
        viewModel.uiState
            .map {
                it.isLoading
            }
            .distinctUntilChanged()
            .onEach { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }.launchIn(lifecycleScope)

        viewModel.uiState
            .map { it.responseResult }
            .filterNotNull()
            .distinctUntilChanged()
            .onEach { result ->
                Log.d("InputViewModel", "Result: $result")
                when (result) {
                    is NetworkResult.Success -> {
                        val material = StudyMaterials.fromSummaryDto(
                            summaryDto = result.data,
                            type = viewModel.uiState.value.inputType.getMaterialType(),
                            input = viewModel.currentText,
                            userId = FirebaseAuthHelper.getCurrentUserUid() ?: ""
                        )
                        viewModel.saveToDatabase(material, result.data.toDomain())
                        ResultActivity.start(
                            this@InputActivity,
                            material
                        )
                        showSuccessAndFinish()
                    }

                    is NetworkResult.Error -> {
                        Snackbar.make(
                            binding.root,
                            result.message,
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    is NetworkResult.Loading -> {
                        // Already handled by isLoading state
                    }
                }
            }
            .launchIn(lifecycleScope)

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (!viewModel.isLoading) {
                        finish()
                    } else {
                        Snackbar.make(
                            binding.root,
                            R.string.please_wait,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            })
    }

    private fun handleOnClick() {
        binding.btnBack.setOnClickListener {
            if (!viewModel.isLoading) {
                finish()
            } else {
                Snackbar.make(
                    binding.root,
                    R.string.please_wait,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        binding.btnSubmit.setOnClickListener {
            if (viewModel.isLoading) {
                return@setOnClickListener
            }

            when (viewModel.currentInputType) {
                INPUT_TYPE_FILE -> {
                    if (viewModel.selectedFileUri != null) {
                        if (viewModel.isValidFile(this, viewModel.selectedFileUri)) {
                            viewModel.generateStudy(this)
                        } else {
                            Snackbar.make(
                                binding.root,
                                R.string.error_invalid_file,
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Snackbar.make(
                            binding.root,
                            R.string.error_no_file_selected,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }

                INPUT_TYPE_LINK -> {
                    if (viewModel.isValidLink()) {
                        viewModel.generateStudy(this)
                    } else {
                        Snackbar.make(
                            binding.root,
                            R.string.error_invalid_link,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }

                INPUT_TYPE_TEXT -> {
                    if (viewModel.isValidText()) {
                        viewModel.generateStudy(this)
                    } else {
                        Snackbar.make(
                            binding.root,
                            R.string.error_invalid_text,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        binding.btnSelectFile.setOnClickListener {
            selectFile()
        }

        binding.btnPaste.setOnClickListener {
            pasteFromClipboard()
        }
    }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                if (viewModel.isValidFile(this, it)) {
                    viewModel.setSelectedFile(it)
                    binding.tvSelectedFile.text = getFileNameFromUri(it)
                    binding.tvNoFileSelected.visibility = View.GONE
                    binding.fileInfoCard.visibility = View.VISIBLE
                } else {
                    Snackbar.make(
                        binding.root,
                        R.string.error_invalid_file,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }

    private fun selectFile() {
        getContent.launch("application/pdf")
    }

    private fun pasteFromClipboard() {
        val clipboard =
            getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = clipboard.primaryClip
        if (clip != null && clip.itemCount > 0) {
            val text = clip.getItemAt(0).text.toString()
            binding.etLink.setText(text)
            viewModel.updateLink(text)
        }
    }

    private fun getFileNameFromUri(uri: Uri): String {
        val contentResolver = applicationContext.contentResolver
        val cursor = contentResolver.query(uri, null, null, null, null)

        return cursor?.use {
            val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            it.moveToFirst()
            if (nameIndex != -1) it.getString(nameIndex) else uri.lastPathSegment ?: "Unknown file"
        } ?: uri.lastPathSegment ?: "Unknown file"
    }

    private fun showSuccessAndFinish() {
        Snackbar.make(
            binding.root,
            R.string.upload_success,
            Snackbar.LENGTH_SHORT
        ).show()

        finish()
    }

    companion object {
        const val INPUT_TYPE_FILE = "file"
        const val INPUT_TYPE_LINK = "link"
        const val INPUT_TYPE_TEXT = "text"

        const val ARG_INPUT_TYPE = "arg_input_type"
    }
}