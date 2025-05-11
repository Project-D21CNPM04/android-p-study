package com.example.pstudy.view.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import com.example.base.ui.base.BindingActivity
import com.example.pstudy.R
import com.example.pstudy.data.firebase.FirebaseAuthHelper
import com.example.pstudy.databinding.ActivitySettingsBinding
import dagger.hilt.android.AndroidEntryPoint

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
            // TODO: Implement reminder functionality
        }

        binding.tvSupport.setOnClickListener {
            // TODO: Implement contact support functionality
        }

        binding.tvAboutUs.setOnClickListener {
            // TODO: Implement about us functionality
        }

        binding.tvTerms.setOnClickListener {
            // TODO: Implement terms functionality
        }

        binding.tvPrivacy.setOnClickListener {
            // TODO: Implement privacy policy functionality
        }
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, SettingsActivity::class.java))
        }
    }
}