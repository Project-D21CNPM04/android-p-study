package com.example.pstudy.view.authentication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import com.example.base.ui.base.BindingActivity
import com.example.pstudy.data.firebase.FirebaseAuthHelper
import com.example.pstudy.databinding.ActivityLoginBinding
import com.example.pstudy.view.home.HomeActivity

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
            FirebaseAuthHelper.loginUser(
                context = this@LoginActivity,
                email = edtEmail.text.toString(),
                password = edtPassword.text.toString()
            ) { isSuccess, message ->
                Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
                if (isSuccess) {
                    navigateToHome()
                }
            }
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