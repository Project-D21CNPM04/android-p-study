package com.example.pstudy.view.result.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.activityViewModels
import com.example.base.ui.base.BindingFragmentLazyPager
import com.example.pstudy.databinding.FragmentRecordBinding
import com.example.pstudy.view.result.ResultViewModel

class RecordFragment : BindingFragmentLazyPager<FragmentRecordBinding>() {

    private val viewModel: ResultViewModel by activityViewModels()

    override fun inflateBinding(inflater: LayoutInflater): FragmentRecordBinding {
        return FragmentRecordBinding.inflate(inflater)
    }

    override fun updateUI() {
        println("RecordFragment updateUI called")
        // TODO: Observe viewModel LiveData here
        // Example: viewModel.recordData.observe(viewLifecycleOwner) { ... }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initial setup using the shared viewModel if needed
    }

    companion object {
        fun newInstance() = RecordFragment()
    }
}
