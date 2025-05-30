package com.example.pstudy.view.authentication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import android.view.View
import com.example.base.ui.base.BindingActivity
import com.example.pstudy.data.firebase.FirebaseAuthHelper
import com.example.pstudy.databinding.ActivityLoginBinding
import com.example.pstudy.view.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BindingActivity<ActivityLoginBinding>() {
    override fun inflateBinding(layoutInflater: LayoutInflater) =
        ActivityLoginBinding.inflate(layoutInflater)

    override fun updateUI(savedInstanceState: Bundle?) {
        checkUser()
        handleOnClick()
    }

    private fun checkUser() {
        if (FirebaseAuthHelper.isUserLoggedIn()) {
            navigateToHome()
        }
    }

    private fun handleOnClick() {
        with(binding) {
            tvLogin.setOnClickListener {
                handleLogin()
            }

            tvSignUp.setOnClickListener {
                navigateToSignUp()
            }
        }
    }

    private fun handleLogin() {
        with(binding) {
            showLoading(true)
            FirebaseAuthHelper.loginUser(
                context = this@LoginActivity,
                email = edtEmail.text.toString(),
                password = edtPassword.text.toString()
            ) { isSuccess, message ->
                showLoading(false)
                Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
                if (isSuccess) {
                    navigateToHome()
                }
            }
        }
    }

    private fun showLoading(show: Boolean) {
        with(binding) {
            lavLoading.visibility = if (show) View.VISIBLE else View.GONE
            tvLogin.isEnabled = !show
            tvSignUp.isEnabled = !show
            edtEmail.isEnabled = !show
            edtPassword.isEnabled = !show
        }
    }

    private fun navigateToHome() {
        HomeActivity.start(this)
    }

    private fun navigateToSignUp() {
        SignUpActivity.start(this)
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, LoginActivity::class.java))
        }
    }
}