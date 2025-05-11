package com.example.pstudy.view.input.fragments

import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.example.pstudy.R
import com.example.pstudy.databinding.FragmentPhotoInputBinding

class PhotoInputFragment : BaseInputFragment<FragmentPhotoInputBinding>() {

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                displaySelectedImage(uri)
                viewModel.setSelectedPhoto(uri)
            }
        }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentPhotoInputBinding.inflate(inflater, container, false)

    override fun setupViews() {
        binding.btnSelectPhoto.setOnClickListener {
            launchPhotoPicker()
        }
    }

    override fun observeViewModel() {
        // No specific observations needed for photo fragment
    }

    override fun validateAndSubmit() {
        if (viewModel.selectedPhotoUri != null) {
            viewModel.generateStudy(requireContext())
        } else {
            showSnackbar(R.string.no_photo_selected)
        }
    }

    private fun launchPhotoPicker() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun displaySelectedImage(uri: Uri) {
        binding.tvNoPhotoSelected.visibility = View.GONE
        binding.photoPreviewCard.visibility = View.VISIBLE
        binding.ivPhotoPreview.setImageURI(uri)
    }

    companion object {
        fun newInstance() = PhotoInputFragment()
    }
}