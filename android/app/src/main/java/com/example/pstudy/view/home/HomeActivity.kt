package com.example.pstudy.view.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.example.base.ui.base.BindingActivity
import com.example.base.utils.startActivityAsRoot
import com.example.pstudy.data.FirebaseAuthHelper
import com.example.pstudy.databinding.ActivityHomeBinding

class HomeActivity : BindingActivity<ActivityHomeBinding>() {
    override fun inflateBinding(layoutInflater: LayoutInflater) =
        ActivityHomeBinding.inflate(layoutInflater)

    override fun updateUI(savedInstanceState: Bundle?) {
        with(binding) {
            tvName.text = FirebaseAuthHelper.getCurrentUserName()
            tvMail.text = FirebaseAuthHelper.getCurrentUserEmail()
            tvUid.text = FirebaseAuthHelper.getCurrentUserUid()
        }
    }

    companion object {
        fun start(context: Context) {
            context.startActivityAsRoot(HomeActivity::class.java)
        }
    }
}