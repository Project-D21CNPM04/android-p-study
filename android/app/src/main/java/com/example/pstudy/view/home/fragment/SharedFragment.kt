package com.example.pstudy.view.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.example.base.ui.base.BindingFragment
import com.example.pstudy.databinding.FragmentSharedBinding


class SharedFragment : BindingFragment<FragmentSharedBinding>() {
    override fun inflateBinding(inflater: LayoutInflater) =
        FragmentSharedBinding.inflate(inflater)

    override fun updateUI(view: View, savedInstanceState: Bundle?) {}

}