package com.example.pstudy.view.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import com.example.base.ui.base.BindingActivity
import com.example.pstudy.data.FirebaseAuthHelper
import com.example.pstudy.databinding.ActivityLoginBinding

class LoginActivity : BindingActivity<ActivityLoginBinding>() {
    override fun inflateBinding(layoutInflater: LayoutInflater) =
        ActivityLoginBinding.inflate(layoutInflater)

    override fun updateUI(savedInstanceState: Bundle?) {
        handleOnClick()
    }

    private fun handleOnClick() {
        with(binding) {
            tvLogin.setOnClickListener {
                handleLogin()
            }
        }
    }

    private fun handleLogin() {
        with(binding) {
            FirebaseAuthHelper.registerUser(
                context = this@LoginActivity,
                email = edtEmail.text.toString(),
                password = edtPassword.text.toString(),
            ) { isSuccess, message ->
                Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
                if (isSuccess) {
//                    navigateToHome()
                }
            }
        }
    }

    private fun navigateToHome() {
        // TODO: handle navigate to home screen
    }
}