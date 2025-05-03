package com.example.pstudy.view.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.example.base.ui.base.BindingFragment
import com.example.pstudy.databinding.FragmentFolderBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FolderFragment : BindingFragment<FragmentFolderBinding>() {
    override fun inflateBinding(inflater: LayoutInflater) =
        FragmentFolderBinding.inflate(inflater)

    override fun updateUI(view: View, savedInstanceState: Bundle?) {

    }

}