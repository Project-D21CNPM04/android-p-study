package com.example.pstudy.view.input

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import com.example.base.ui.base.BindingActivity
import com.example.pstudy.R
import com.example.pstudy.databinding.ActivityInputBinding
import com.google.android.material.snackbar.Snackbar

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
        // Observe ViewModel states if needed
    }

    private fun handleOnClick() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnSubmit.setOnClickListener {
            when (viewModel.currentInputType) {
                INPUT_TYPE_FILE -> {
                    if (viewModel.selectedFileUri != null) {
                        // TODO: Implement upload file to server logic
                        // uploadFileToServer(viewModel.selectedFileUri)
                        showSuccessAndFinish()
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
                        // TODO: Implement upload link to server logic
                        // uploadLinkToServer(viewModel.currentLink)
                        showSuccessAndFinish()
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
                        // TODO: Implement upload text to server logic
                        // uploadTextToServer(viewModel.currentText)
                        showSuccessAndFinish()
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
                viewModel.setSelectedFile(it)
                binding.tvSelectedFile.text = getFileNameFromUri(it)
                binding.tvNoFileSelected.visibility = View.GONE
                binding.fileInfoCard.visibility = View.VISIBLE
            }
        }

    private fun selectFile() {
        getContent.launch("*/*")
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

        // Close this activity after a short delay to show success message
        binding.root.postDelayed({
            finish()
        }, 1500)
    }

    companion object {
        const val INPUT_TYPE_FILE = "file"
        const val INPUT_TYPE_LINK = "link"
        const val INPUT_TYPE_TEXT = "text"

        const val ARG_INPUT_TYPE = "arg_input_type"
    }
}