package com.example.pstudy.data

import android.content.Context
import com.example.pstudy.R
import com.google.firebase.auth.FirebaseAuth

object FirebaseAuthHelper {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    fun registerUser(
        context: Context,
        email: String,
        password: String,
        onResult: (Boolean, String) -> Unit
    ) {
        if (email.isEmpty() || password.isEmpty()) {
            onResult(false, context.getString(R.string.empty_information_msg))
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, context.getString(R.string.sign_up_success_msg))
                } else {
                    onResult(false, task.exception?.message ?: context.getString(R.string.sign_up_fail_msg))
                }
            }
    }
}