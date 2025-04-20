package com.example.pstudy.view.result.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.activityViewModels
import com.example.base.ui.base.BindingFragmentLazyPager
import com.example.pstudy.databinding.FragmentSummaryBinding
import com.example.pstudy.view.result.ResultViewModel

class SummaryFragment : BindingFragmentLazyPager<FragmentSummaryBinding>() {

    private val viewModel: ResultViewModel by activityViewModels()

    override fun inflateBinding(inflater: LayoutInflater): FragmentSummaryBinding {
        return FragmentSummaryBinding.inflate(inflater)
    }

    override fun updateUI() {
        println("SummaryFragment updateUI called")
        // TODO: Observe viewModel LiveData here
        // Example: viewModel.summaryData.observe(viewLifecycleOwner) { data -> binding.someTextView.text = data.info }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initial setup using the shared viewModel if needed
    }

    companion object {
        fun newInstance() = SummaryFragment()
    }
}
