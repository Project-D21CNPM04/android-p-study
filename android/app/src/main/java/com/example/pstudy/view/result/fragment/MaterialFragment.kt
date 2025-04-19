package com.example.pstudy.view.result.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.activityViewModels
import com.example.base.ui.base.BindingFragmentLazyPager
import com.example.pstudy.databinding.FragmentMaterialBinding
import com.example.pstudy.view.result.ResultViewModel

class MaterialFragment : BindingFragmentLazyPager<FragmentMaterialBinding>() {

    private val viewModel: ResultViewModel by activityViewModels()

    override fun inflateBinding(inflater: LayoutInflater): FragmentMaterialBinding {
        return FragmentMaterialBinding.inflate(inflater)
    }

    override fun updateUI() {
        println("MaterialFragment updateUI called")
        // TODO: Observe viewModel LiveData here
        // Example: viewModel.materialData.observe(viewLifecycleOwner) { ... }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initial setup using the shared viewModel if needed
    }

    companion object {
        fun newInstance() = MaterialFragment()
    }
}
