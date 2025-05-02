package com.example.pstudy.data.firebase

import android.content.Context
import com.example.pstudy.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest

object FirebaseAuthHelper {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val currentUser
        get() = auth.currentUser

    fun getCurrentUserName(): String? = currentUser?.displayName
    fun getCurrentUserEmail(): String? = currentUser?.email
    fun getCurrentUserUid(): String? = currentUser?.uid

    fun isUserLoggedIn() = currentUser != null

    fun registerUser(
        context: Context,
        name: String,
        email: String,
        password: String,
        onResult: (Boolean, String) -> Unit
    ) {
        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            onResult(false, context.getString(R.string.empty_information_msg))
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    auth.currentUser?.let {
                        val profileUpdates = userProfileChangeRequest {
                            displayName = name
                        }
                        it.updateProfile(profileUpdates)
                            .addOnCompleteListener { updateTask ->
                                if (updateTask.isSuccessful) {
                                    onResult(true, context.getString(R.string.sign_up_success_msg))
                                }
                            }
                    }
                } else {
                    onResult(
                        false,
                        task.exception?.message ?: context.getString(R.string.sign_up_fail_msg)
                    )
                }
            }
    }

    fun loginUser(
        context: Context,
        email: String,
        password: String,
        onResult: (Boolean, String) -> Unit
    ) {
        if (email.isEmpty() || password.isEmpty()) {
            onResult(false, context.getString(R.string.empty_information_msg))
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, context.getString(R.string.login_success_msg))
                } else {
                    onResult(
                        false,
                        task.exception?.message ?: context.getString(R.string.login_fail_msg)
                    )
                }
            }
    }
}