package com.example.pstudy.view.folder.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pstudy.data.model.Folder
import com.example.pstudy.databinding.ItemFolderSelectionBinding

class FolderSelectionAdapter :
    ListAdapter<Folder, FolderSelectionAdapter.ViewHolder>(FolderDiffCallback()) {

    private var selectedPosition = -1
    private var selectedFolder: Folder? = null
    private var preSelectedFolderId: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFolderSelectionBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    fun getSelectedFolder(): Folder? {
        return selectedFolder
    }

    fun setPreselectedFolder(folderId: String?) {
        preSelectedFolderId = folderId
    }

    override fun submitList(list: List<Folder>?) {
        super.submitList(list)
        // If we have a preselected folder ID, find and select it
        if (preSelectedFolderId != null && list != null) {
            val folderPosition = list.indexOfFirst { it.id == preSelectedFolderId }
            if (folderPosition >= 0) {
                selectedPosition = folderPosition
                selectedFolder = list[folderPosition]
            }
        }
    }

    inner class ViewHolder(private val binding: ItemFolderSelectionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(folder: Folder, position: Int) {
            binding.radioFolder.text = folder.name
            binding.radioFolder.isChecked = position == selectedPosition

            // Make the whole item clickable to select the folder
            binding.root.setOnClickListener {
                val previousSelected = selectedPosition
                selectedPosition = position
                selectedFolder = folder
                notifyItemChanged(previousSelected)
                notifyItemChanged(selectedPosition)
            }
        }
    }
}

class FolderDiffCallback : DiffUtil.ItemCallback<Folder>() {
    override fun areItemsTheSame(oldItem: Folder, newItem: Folder): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Folder, newItem: Folder): Boolean {
        return oldItem == newItem
    }
}