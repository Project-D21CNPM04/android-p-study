package com.example.pstudy.view.result.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.base.ui.base.BindingFragmentLazyPager
import com.example.pstudy.databinding.FragmentSummaryBinding
import com.example.pstudy.view.result.ResultViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SummaryFragment : BindingFragmentLazyPager<FragmentSummaryBinding>() {

    private val viewModel: ResultViewModel by activityViewModels()
    private lateinit var markwon: Markwon

    override fun inflateBinding(inflater: LayoutInflater): FragmentSummaryBinding {
        return FragmentSummaryBinding.inflate(inflater)
    }

    override fun updateUI() {
        setupMarkwon()
        observeViewState()
    }

    private fun setupMarkwon() {
        markwon = Markwon.create(requireContext())
    }

    private fun observeViewState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.viewState
                .map { Triple(it.summary?.content, it.isSummaryLoading, it.resultTitle) }
                .distinctUntilChanged()
                .collect { (summaryContent, isSummaryLoading, title) ->
                    if (isSummaryLoading) {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.tvSummary.visibility = View.GONE
                        binding.tvEmptyState.visibility = View.GONE
                    } else {
                        binding.progressBar.visibility = View.GONE

                        if (summaryContent.isNullOrEmpty()) {
                            binding.tvSummary.visibility = View.GONE
                            binding.tvEmptyState.visibility = View.VISIBLE
                            binding.tvEmptyState.text = "No summary available for '$title'"
                        } else {
                            binding.tvSummary.visibility = View.VISIBLE
                            binding.tvEmptyState.visibility = View.GONE
                            markwon.setMarkdown(binding.tvSummary, summaryContent)
                        }
                    }
                }
        }
    }

    companion object {
        fun newInstance() = SummaryFragment()
    }
}
