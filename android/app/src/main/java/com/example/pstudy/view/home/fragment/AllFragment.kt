package com.example.pstudy.view.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.example.base.ui.base.BindingFragment
import com.example.pstudy.databinding.FragmentAllBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllFragment : BindingFragment<FragmentAllBinding>() {
    override fun inflateBinding(inflater: LayoutInflater) = FragmentAllBinding.inflate(inflater)

    override fun updateUI(view: View, savedInstanceState: Bundle?) {}

}