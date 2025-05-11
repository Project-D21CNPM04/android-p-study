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
import androidx.fragment.app.Fragment
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
import com.example.pstudy.view.input.fragments.AudioInputFragment
import com.example.pstudy.view.input.fragments.FileInputFragment
import com.example.pstudy.view.input.fragments.LinkInputFragment
import com.example.pstudy.view.input.fragments.PhotoInputFragment
import com.example.pstudy.view.input.fragments.TextInputFragment
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
        updateTitle()
        loadInputFragment()
    }

    private fun updateTitle() {
        when (viewModel.currentInputType) {
            INPUT_TYPE_FILE -> {
                binding.tvTitle.text = getString(R.string.input_file_title)
                binding.btnSubmit.text = getString(R.string.input_file_submit)
            }
            INPUT_TYPE_LINK -> {
                binding.tvTitle.text = getString(R.string.input_link_title)
                binding.btnSubmit.text = getString(R.string.input_link_submit)
            }
            INPUT_TYPE_TEXT -> {
                binding.tvTitle.text = getString(R.string.input_text_title)
                binding.btnSubmit.text = getString(R.string.input_text_submit)
            }
            INPUT_TYPE_AUDIO -> {
                binding.tvTitle.text = getString(R.string.input_audio_title)
                binding.btnSubmit.text = getString(R.string.input_audio_submit)
            }
            INPUT_TYPE_PHOTO -> {
                binding.tvTitle.text = getString(R.string.input_photo_title)
                binding.btnSubmit.text = getString(R.string.input_photo_submit)
            }
            else -> finish()
        }
    }

    private fun loadInputFragment() {
        val fragment: Fragment = when (viewModel.currentInputType) {
            INPUT_TYPE_FILE -> FileInputFragment.newInstance()
            INPUT_TYPE_LINK -> LinkInputFragment.newInstance()
            INPUT_TYPE_TEXT -> TextInputFragment.newInstance()
            INPUT_TYPE_AUDIO -> AudioInputFragment.newInstance()
            INPUT_TYPE_PHOTO -> PhotoInputFragment.newInstance()
            else -> throw IllegalArgumentException("Unknown input type: ${viewModel.currentInputType}")
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
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

            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)

            when (viewModel.currentInputType) {
                INPUT_TYPE_FILE -> {
                    if (currentFragment is FileInputFragment) {
                        currentFragment.validateAndSubmit()
                    }
                }
                INPUT_TYPE_LINK -> {
                    if (currentFragment is LinkInputFragment) {
                        currentFragment.validateAndSubmit()
                    }
                }
                INPUT_TYPE_TEXT -> {
                    if (currentFragment is TextInputFragment) {
                        currentFragment.validateAndSubmit()
                    }
                }
                INPUT_TYPE_AUDIO -> {
                    if (currentFragment is AudioInputFragment) {
                        currentFragment.validateAndSubmit()
                    }
                }
                INPUT_TYPE_PHOTO -> {
                    if (currentFragment is PhotoInputFragment) {
                        currentFragment.validateAndSubmit()
                    }
                }
            }
        }
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
        const val INPUT_TYPE_AUDIO = "audio"
        const val INPUT_TYPE_PHOTO = "photo"

        const val ARG_INPUT_TYPE = "arg_input_type"
    }
}