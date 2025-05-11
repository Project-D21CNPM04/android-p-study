package com.example.pstudy.view.result.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.base.ui.base.BindingFragmentLazyPager
import com.example.pstudy.databinding.FragmentMindMapBinding
import com.example.pstudy.view.result.ResultViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MindMapFragment : BindingFragmentLazyPager<FragmentMindMapBinding>() {

    private val viewModel: ResultViewModel by activityViewModels()

    override fun inflateBinding(inflater: LayoutInflater): FragmentMindMapBinding {
        return FragmentMindMapBinding.inflate(inflater)
    }

    override fun updateUI() {
        // Initial setup happens in onViewCreated
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWebView()
        setupClickListeners()
        observeViewState()
    }

    private fun setupClickListeners() {
        binding.btnGenerateMindMap.setOnClickListener {
            val studyMaterials = viewModel.viewState.value.result
            if (studyMaterials != null) {
                binding.btnGenerateMindMap.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
                binding.tvEmptyState.visibility = View.GONE
                viewModel.generateMindMap(studyMaterials.id, studyMaterials)
            }
        }
    }

    private fun setupWebView() {
        binding.webViewMindMap.settings.javaScriptEnabled = true
        binding.webViewMindMap.settings.domStorageEnabled = true
        binding.webViewMindMap.settings.loadWithOverviewMode = true
        binding.webViewMindMap.settings.useWideViewPort = true
        binding.webViewMindMap.settings.builtInZoomControls = true
        binding.webViewMindMap.settings.displayZoomControls = false
        binding.webViewMindMap.webViewClient = WebViewClient()

        // Add console message logging
        binding.webViewMindMap.webChromeClient = object : android.webkit.WebChromeClient() {
            override fun onConsoleMessage(message: android.webkit.ConsoleMessage): Boolean {
                Log.d(
                    "WebView",
                    "${message.message()} -- From line ${message.lineNumber()} of ${message.sourceId()}"
                )
                return true
            }
        }
    }

    private fun observeViewState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.viewState
                .map { Triple(it.mindMap?.content, it.isMindMapLoading, it.resultTitle) }
                .distinctUntilChanged()
                .collect { (mindMapContent, isMindMapLoading, title) ->
                    Log.d("GiangPT", "$mindMapContent, $isMindMapLoading, $title")
                    if (isMindMapLoading) {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.webViewMindMap.visibility = View.GONE
                        binding.tvEmptyState.visibility = View.GONE
                        binding.btnGenerateMindMap.visibility = View.GONE
                    } else {
                        binding.progressBar.visibility = View.GONE

                        if (mindMapContent.isNullOrEmpty() && !isMindMapLoading) {
                            binding.webViewMindMap.visibility = View.GONE
                            binding.tvEmptyState.visibility = View.GONE
                            binding.btnGenerateMindMap.visibility = View.VISIBLE
                        } else if (!mindMapContent.isNullOrEmpty()) {
                            binding.webViewMindMap.visibility = View.VISIBLE
                            binding.tvEmptyState.visibility = View.GONE
                            binding.btnGenerateMindMap.visibility = View.GONE
                            Log.d(
                                "GiangPT",
                                "Loading mind map HTML content: ${mindMapContent.take(200)}..."
                            )
                            binding.webViewMindMap.loadDataWithBaseURL(
                                null,
                                mindMapContent,
                                "text/html",
                                "UTF-8",
                                null
                            )
                        }
                    }
                }
        }
    }

    companion object {
        fun newInstance() = MindMapFragment()
    }
}