package com.example.base.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.viewbinding.ViewBinding

abstract class BindingActivity<T : ViewBinding> : KoreActivity() {
    companion object {
        private const val TAG = "BaseActivityBinding"
    }

    protected lateinit var binding: T
        private set

    protected abstract fun inflateBinding(layoutInflater: LayoutInflater): T

    final override fun createContentView(savedInstanceState: Bundle?): View {
        binding = inflateBinding(layoutInflater)
        return binding.root
    }
}
