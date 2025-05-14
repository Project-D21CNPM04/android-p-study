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
import com.example.pstudy.R
import com.example.pstudy.data.model.Folder
import com.example.pstudy.data.model.StudyMaterials
import com.example.pstudy.databinding.DialogSelectFolderBinding
import com.example.pstudy.databinding.FragmentAllBinding
import com.example.pstudy.view.folder.adapter.FolderSelectionAdapter
import com.example.pstudy.view.home.adapter.StudyMaterialsAdapter
import com.example.pstudy.view.home.viewmodel.FolderViewModel
import com.example.pstudy.view.home.viewmodel.HomeViewModel
import com.example.pstudy.view.result.ResultActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

@AndroidEntryPoint
class AllFragment : BindingFragment<FragmentAllBinding>() {
    private val viewModel: HomeViewModel by activityViewModels()
    private val folderViewModel: FolderViewModel by activityViewModels()
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
        adapter = StudyMaterialsAdapter(
            onItemClick = { studyMaterial ->
                navigateToResultActivity(studyMaterial)
            },
            onAddToFolderClick = { studyMaterial ->
                showFolderSelectionDialog(studyMaterial)
            }
        )
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@AllFragment.adapter
        }
    }

    private fun collectUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.homeUiState.collectLatest { state ->
                        // Get all folders to map with study materials
                        val folders =
                            folderViewModel.folderUiState.value.folders.associateBy { it.id }

                        // Update adapter with study materials and folder information
                        adapter.submitListWithFolders(state.studyMaterials, folders)

                        binding.progressBar.isVisible = state.isLoading

                        val isListEmpty = state.studyMaterials.isEmpty()
                        val showEmptyView = isListEmpty && !state.isLoading

                        binding.groupEmpty.isVisible = showEmptyView
                        binding.recyclerView.isVisible = !showEmptyView
                    }
                }
            }
        }
    }

    private fun navigateToResultActivity(studyMaterial: StudyMaterials) {
        ResultActivity.start(requireContext(), studyMaterial)
    }

    private fun showFolderSelectionDialog(studyMaterial: StudyMaterials) {
        val dialogBinding = DialogSelectFolderBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()
            
        // Remove window background to allow custom rounded corners
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            
        val folderAdapter = FolderSelectionAdapter()
        // Preselect folder if this study material already belongs to one
        folderAdapter.setPreselectedFolder(studyMaterial.folderId)

        dialogBinding.recyclerFolders.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = folderAdapter
        }
            
        // Load folders
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                folderViewModel.folderUiState.collect { state ->
                    dialogBinding.apply {
                        if (state.folders.isEmpty()) {
                            tvEmptyFolders.visibility = View.VISIBLE
                            recyclerFolders.visibility = View.GONE
                            // Disable add button when no folders available
                            btnAdd.isEnabled = false
                        } else {
                            tvEmptyFolders.visibility = View.GONE
                            recyclerFolders.visibility = View.VISIBLE
                            btnAdd.isEnabled = true
                            folderAdapter.submitList(state.folders)
                        }
                    }
                }
            }
        }
            
        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
            
        dialogBinding.btnAdd.setOnClickListener {
            val selectedFolder = folderAdapter.getSelectedFolder()
            if (selectedFolder != null) {
                // Add note to selected folder
                folderViewModel.addNotesToFolder(selectedFolder.id, listOf(studyMaterial))
                Toast.makeText(
                    requireContext(),
                    getString(R.string.note_added_to_folder, selectedFolder.name),
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()
                // Update the UI to reflect the change
                collectUiState()
            } else {
                // Show message to select a folder
                Toast.makeText(
                    requireContext(),
                    getString(R.string.please_select_folder),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
            
        dialog.show()
    }
}