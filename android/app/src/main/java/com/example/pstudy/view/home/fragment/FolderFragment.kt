package com.example.pstudy.view.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.base.ui.base.BindingFragment
import com.example.pstudy.R
import com.example.pstudy.data.model.Folder
import com.example.pstudy.databinding.DialogCreateFolderBinding
import com.example.pstudy.databinding.FragmentFolderBinding
import com.example.pstudy.view.folder.FolderActivity
import com.example.pstudy.view.home.adapter.FolderAdapter
import com.example.pstudy.view.home.viewmodel.FolderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FolderFragment : BindingFragment<FragmentFolderBinding>() {
    private val viewModel: FolderViewModel by activityViewModels()
    private lateinit var adapter: FolderAdapter

    override fun inflateBinding(inflater: LayoutInflater) =
        FragmentFolderBinding.inflate(inflater)

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
        binding.llAddFolder.setOnClickListener {
            showCreateFolderDialog()
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadFolders()
        }

        collectUiState()
    }

    private fun setupRecyclerView() {
        adapter = FolderAdapter { folder ->
            navigateToFolderActivity(folder)
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FolderFragment.adapter
        }
    }

    private fun collectUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.folderUiState.collectLatest { state ->
                    adapter.submitList(state.folders)
                    binding.progressBar.isVisible = state.isLoading
                    binding.swipeRefresh.isRefreshing = state.isLoading

                    val isListEmpty = state.folders.isEmpty()
                    val showEmptyView = isListEmpty && !state.isLoading

                    binding.groupEmpty.isVisible = showEmptyView
                    binding.recyclerView.isVisible = !showEmptyView

                    // Show error messages if any
                    state.error?.let { error ->
                        com.google.android.material.snackbar.Snackbar.make(
                            binding.root,
                            error,
                            com.google.android.material.snackbar.Snackbar.LENGTH_LONG
                        ).show()
                    }

                    // Show success messages if any
                    state.successMessage?.let { message ->
                        com.google.android.material.snackbar.Snackbar.make(
                            binding.root,
                            message,
                            com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
                        ).show()
                        // Clear the success message after showing it
                        viewModel.clearSuccessMessage()
                    }
                }
            }
        }
    }

    private fun showCreateFolderDialog() {
        val dialogBinding = DialogCreateFolderBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext(), R.style.TransparentDialog)
            .setView(dialogBinding.root)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.decorView?.setBackgroundResource(android.R.color.transparent)

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnCreate.setOnClickListener {
            val folderName = dialogBinding.etFolderName.text.toString()
            if (folderName.isNotBlank()) {
                viewModel.createFolder(folderName)
                dialog.dismiss()
            } else {
                dialogBinding.tilFolderName.error = getString(R.string.error_folder_name_empty)
            }
        }

        dialog.show()
    }

    private fun navigateToFolderActivity(folder: Folder) {
        FolderActivity.start(requireContext(), folder)
    }
}