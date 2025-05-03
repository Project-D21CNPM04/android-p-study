package com.example.pstudy.view.result.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.activityViewModels
import com.example.base.ui.base.BindingFragmentLazyPager
import com.example.pstudy.databinding.FragmentMindMapBinding
import com.example.pstudy.view.result.ResultViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MindMapFragment : BindingFragmentLazyPager<FragmentMindMapBinding>() {

    private val viewModel: ResultViewModel by activityViewModels()

    override fun inflateBinding(inflater: LayoutInflater): FragmentMindMapBinding {
        return FragmentMindMapBinding.inflate(inflater)
    }

    override fun updateUI() {
        println("MindMapFragment updateUI called")
        // TODO: Observe viewModel LiveData here
        // Example: viewModel.mindMapData.observe(viewLifecycleOwner) { ... }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initial setup using the shared viewModel if needed
    }

    companion object {
        fun newInstance() = MindMapFragment()
    }
}
