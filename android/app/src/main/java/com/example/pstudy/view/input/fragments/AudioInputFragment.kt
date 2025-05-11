package com.example.pstudy.view.input.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.pstudy.R
import com.example.pstudy.databinding.FragmentAudioInputBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AudioInputFragment : BaseInputFragment<FragmentAudioInputBinding>() {
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var audioFilePath: String? = null
    private var isRecording = false
    private var recordingDuration = 0
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startRecording()
        } else {
            showSnackbar(R.string.permission_denied)
        }
    }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentAudioInputBinding.inflate(inflater, container, false)

    override fun setupViews() {
        binding.fabRecord.setOnClickListener {
            checkPermissionAndRecord()
        }

        binding.fabStopRecord.setOnClickListener {
            stopRecording()
        }

        binding.fabPlayRecord.setOnClickListener {
            playRecording()
        }

        runnable = Runnable {
            if (isRecording) {
                updateRecordingTimer()
                handler.postDelayed(runnable, 1000)
            }
        }
    }

    override fun observeViewModel() {
        // No specific observations needed for audio fragment
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseMediaRecorder()
        releaseMediaPlayer()
    }

    override fun validateAndSubmit() {
        if (audioFilePath != null && File(audioFilePath!!).exists()) {
            // Submit the recorded audio
            viewModel.setAudioFile(audioFilePath!!)
            viewModel.generateStudy(requireContext())
        } else {
            showSnackbar(R.string.no_recording_available)
        }
    }

    private fun checkPermissionAndRecord() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                startRecording()
            }

            else -> {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    private fun startRecording() {
        try {
            releaseMediaRecorder()

            val file = File(requireContext().getExternalFilesDir(null), "audio_recording.mp3")
            audioFilePath = file.absolutePath

            recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(requireContext())
            } else {
                MediaRecorder()
            }

            recorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(audioFilePath)
                prepare()
                start()
            }

            isRecording = true
            recordingDuration = 0

            binding.tvRecordingStatus.text = getString(R.string.recording)
            binding.tvRecordingTimer.visibility = View.VISIBLE
            binding.recordingControlsLayout.visibility = View.VISIBLE
            binding.tvRecordingTimer.text = "00:00"

            handler.post(runnable)
        } catch (e: IOException) {
            Log.e(TAG, "Failed to prepare media recorder", e)
            showSnackbar("Failed to start recording")
        }
    }

    private fun stopRecording() {
        if (isRecording) {
            recorder?.apply {
                stop()
                release()
            }
            recorder = null
            isRecording = false
            handler.removeCallbacks(runnable)

            binding.tvRecordingStatus.text = getString(R.string.ready_to_record)
            binding.audioInfoCard.visibility = View.VISIBLE
            val duration =
                String.format("%02d:%02d", recordingDuration / 60, recordingDuration % 60)
            binding.tvAudioInfo.text = "Recording ($duration)"
        }
    }

    private fun playRecording() {
        if (audioFilePath != null) {
            try {
                releaseMediaPlayer()
                player = MediaPlayer().apply {
                    setDataSource(audioFilePath)
                    prepare()
                    start()
                }
            } catch (e: IOException) {
                Log.e(TAG, "Failed to prepare media player", e)
                showSnackbar("Failed to play recording")
            }
        }
    }

    private fun updateRecordingTimer() {
        recordingDuration++
        val minutes = recordingDuration / 60
        val seconds = recordingDuration % 60
        binding.tvRecordingTimer.text = String.format("%02d:%02d", minutes, seconds)
    }

    private fun releaseMediaRecorder() {
        if (isRecording) {
            try {
                recorder?.stop()
            } catch (e: Exception) {
                Log.e(TAG, "Error stopping recorder", e)
            }
        }
        recorder?.release()
        recorder = null
        isRecording = false
    }

    private fun releaseMediaPlayer() {
        player?.release()
        player = null
    }

    companion object {
        private const val TAG = "AudioInputFragment"

        fun newInstance() = AudioInputFragment()
    }
}