package com.example.pstudy.view.authentication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.example.base.ui.base.BindingActivity
import com.example.pstudy.data.firebase.FirebaseAuthHelper
import com.example.pstudy.databinding.ActivitySignUpBinding
import com.example.pstudy.view.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpActivity : BindingActivity<ActivitySignUpBinding>() {
    override fun inflateBinding(layoutInflater: LayoutInflater) =
        ActivitySignUpBinding.inflate(layoutInflater)

    override fun updateUI(savedInstanceState: Bundle?) {
        handleOnClick()
    }

    private fun handleOnClick() {
        with(binding) {
            imvBack.setOnClickListener {
                navigateToLogin()
            }
            tvSignUp.setOnClickListener {
                handleSignUp()
            }
        }
    }

    private fun handleSignUp() {
        with(binding) {
            showLoading(true)
            FirebaseAuthHelper.registerUser(
                context = this@SignUpActivity,
                name = edtName.text.toString(),
                email = edtEmail.text.toString(),
                password = edtPassword.text.toString(),
            ) { isSuccess, message ->
                showLoading(false)
                Toast.makeText(this@SignUpActivity, message, Toast.LENGTH_SHORT).show()
                if (isSuccess) {
                    navigateToHome()
                }
            }
        }
    }

    private fun navigateToHome() {
        HomeActivity.start(this)
    }

    private fun navigateToLogin() {
        LoginActivity.start(this)
        finish()
    }

    private fun showLoading(show: Boolean) {
        with(binding) {
            lavLoading.visibility = if (show) View.VISIBLE else View.GONE
            tvSignUp.isEnabled = !show
            imvBack.isEnabled = !show
            edtName.isEnabled = !show
            edtEmail.isEnabled = !show
            edtPassword.isEnabled = !show
        }
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, SignUpActivity::class.java))
        }
    }
}