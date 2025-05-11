package com.example.pstudy.view.input.fragments

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.pstudy.R
import com.example.pstudy.databinding.FragmentPhotoInputBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PhotoInputFragment : BaseInputFragment<FragmentPhotoInputBinding>() {

    private var capturedImageUri: Uri? = null

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                displaySelectedImage(uri)
                viewModel.setSelectedPhoto(uri)
            }
        }

    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                captureImage()
            } else {
                showSnackbar(R.string.camera_permission_denied)
            }
        }

    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && capturedImageUri != null) {
            displaySelectedImage(capturedImageUri!!)
            viewModel.setSelectedPhoto(capturedImageUri!!)
        }
    }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentPhotoInputBinding.inflate(inflater, container, false)

    override fun setupViews() {
        binding.btnSelectPhoto.setOnClickListener {
            launchPhotoPicker()
        }
        
        binding.btnTakePhoto.setOnClickListener {
            checkCameraPermissionAndCapture()
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
    
    private fun checkCameraPermissionAndCapture() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                captureImage()
            }
            else -> {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun captureImage() {
        try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "IMG_$timestamp.jpg"

            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            }

            val contentResolver = requireContext().contentResolver
            capturedImageUri = contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )

            capturedImageUri?.let {
                takePicture.launch(it)
            } ?: showSnackbar("Unable to create image file")
            
        } catch (e: Exception) {
            showSnackbar("Error launching camera: ${e.message}")
        }
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