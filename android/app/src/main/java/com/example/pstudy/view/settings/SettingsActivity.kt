package com.example.pstudy.view.settings

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.base.ui.base.BindingActivity
import com.example.pstudy.R
import com.example.pstudy.data.firebase.FirebaseAuthHelper
import com.example.pstudy.data.notification.NotificationHelper
import com.example.pstudy.databinding.ActivitySettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Random

@AndroidEntryPoint
class SettingsActivity : BindingActivity<ActivitySettingsBinding>() {

    override fun inflateBinding(layoutInflater: LayoutInflater) =
        ActivitySettingsBinding.inflate(layoutInflater)

    override fun getStatusBarColor() = R.color.black

    override fun updateUI(savedInstanceState: Bundle?) {
        setupUserInfo()
        setupClickListeners()
    }

    private fun setupUserInfo() {
        binding.tvUserName.text = FirebaseAuthHelper.getCurrentUserName()
    }

    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.tvReminder.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                checkNotificationPermission()
            } else {
                showReminderDialog()
            }
        }

        binding.tvSupport.setOnClickListener {
        }

        binding.tvAboutUs.setOnClickListener {
        }

        binding.tvTerms.setOnClickListener {
        }

        binding.tvPrivacy.setOnClickListener {
        }
    }

    private fun showReminderDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_reminder, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        val etContent =
            dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etContent)
        val tvDateValue = dialogView.findViewById<android.widget.TextView>(R.id.tvDateValue)
        val tvTimeValue = dialogView.findViewById<android.widget.TextView>(R.id.tvTimeValue)
        val switchRepeatDaily =
            dialogView.findViewById<com.google.android.material.switchmaterial.SwitchMaterial>(R.id.switchRepeatDaily)
        val btnSetReminder = dialogView.findViewById<android.widget.Button>(R.id.btnSetReminder)

        val calendar = Calendar.getInstance().apply {
            add(Calendar.HOUR_OF_DAY, 1)
        }

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        tvDateValue.text = dateFormat.format(calendar.time)
        tvTimeValue.text = timeFormat.format(calendar.time)

        tvDateValue.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    tvDateValue.text = dateFormat.format(calendar.time)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        tvTimeValue.setOnClickListener {
            TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    calendar.set(Calendar.SECOND, 0)
                    tvTimeValue.text = timeFormat.format(calendar.time)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        btnSetReminder.setOnClickListener {
            val content = etContent.text?.toString()?.trim() ?: ""
            val repeatDaily = switchRepeatDaily.isChecked
            val timeInMillis = calendar.timeInMillis

            when {
                content.isEmpty() -> {
                    Toast.makeText(this, R.string.empty_content, Toast.LENGTH_SHORT).show()
                }

                !NotificationHelper(this).isValidReminderTime(timeInMillis) -> {
                    Toast.makeText(this, R.string.invalid_time, Toast.LENGTH_SHORT).show()
                }

                else -> {
                    val notificationId = Random().nextInt(1000)

                    val isScheduled = NotificationHelper(this).scheduleReminder(
                        notificationId,
                        content,
                        timeInMillis,
                        repeatDaily
                    )

                    if (isScheduled) {
                        Toast.makeText(this, R.string.reminder_set_success, Toast.LENGTH_SHORT)
                            .show()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(this, R.string.reminder_set_error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        dialog.show()
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, SettingsActivity::class.java))
        }

        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 123
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showReminderDialog()
        } else {
            Toast.makeText(this, R.string.reminder_set_error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    showReminderDialog()
                }

                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    AlertDialog.Builder(this)
                        .setTitle(R.string.permission_required)
                        .setMessage(R.string.notification_channel_description)
                        .setPositiveButton(android.R.string.ok) { _, _ ->
                            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                        .setNegativeButton(android.R.string.cancel, null)
                        .show()
                }

                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            showReminderDialog()
        }
    }
}