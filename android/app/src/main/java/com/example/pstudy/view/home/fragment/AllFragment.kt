package com.example.pstudy.view.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.base.ui.base.BindingFragment
import com.example.pstudy.data.model.StudyMaterials
import com.example.pstudy.databinding.FragmentAllBinding
import com.example.pstudy.view.home.adapter.StudyMaterialsAdapter
import com.example.pstudy.view.home.viewmodel.HomeViewModel
import com.example.pstudy.view.result.ResultActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AllFragment : BindingFragment<FragmentAllBinding>() {
    private val viewModel: HomeViewModel by activityViewModels()
    private lateinit var adapter: StudyMaterialsAdapter

    override fun inflateBinding(inflater: LayoutInflater) = FragmentAllBinding.inflate(inflater)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        setupRecyclerView()
        return view
    }

    override fun updateUI(view: View, savedInstanceState: Bundle?) {
        collectUiState()
    }

    private fun setupRecyclerView() {
        adapter = StudyMaterialsAdapter { studyMaterial ->
            navigateToResultActivity(studyMaterial)
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@AllFragment.adapter
        }
    }

    private fun collectUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.homeUiState.collectLatest { state ->
                    adapter.submitList(state.studyMaterials)
                    binding.progressBar.isVisible = state.isLoading

                    val isListEmpty = state.studyMaterials.isEmpty()
                    val showEmptyView = isListEmpty && !state.isLoading

                    binding.groupEmpty.isVisible = showEmptyView
                    binding.recyclerView.isVisible = !showEmptyView
                }
            }
        }
    }

    private fun navigateToResultActivity(studyMaterial: StudyMaterials) {
        ResultActivity.start(requireContext(), studyMaterial)
    }
}