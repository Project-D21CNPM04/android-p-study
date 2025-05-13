package com.example.pstudy.view.folder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pstudy.data.model.Note
import com.example.pstudy.data.model.StudyMaterials
import com.example.pstudy.databinding.ItemNoteWithCheckboxBinding

class NoteSelectionAdapter :
    ListAdapter<StudyMaterials, NoteSelectionAdapter.NoteViewHolder>(NoteDiffCallback()) {

    private val selectedNotes = mutableSetOf<String>()

    fun getSelectedNotes(): List<StudyMaterials> {
        return currentList.filter { selectedNotes.contains(it.id) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteWithCheckboxBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NoteViewHolder(private val binding: ItemNoteWithCheckboxBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    toggleSelection(getItem(position).id)
                    binding.checkbox.isChecked = isSelected(getItem(position).id)
                }
            }

            binding.checkbox.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    toggleSelection(getItem(position).id)
                }
            }
        }

        fun bind(studyMaterial: StudyMaterials) {
            binding.tvTitle.text =
                if (studyMaterial.title.isNotEmpty()) studyMaterial.title else "Untitled"
            binding.tvContent.text =
                studyMaterial.input.take(100) + if (studyMaterial.input.length > 100) "..." else ""
            binding.checkbox.isChecked = isSelected(studyMaterial.id)
        }
    }

    private fun isSelected(id: String): Boolean {
        return selectedNotes.contains(id)
    }

    private fun toggleSelection(id: String) {
        if (selectedNotes.contains(id)) {
            selectedNotes.remove(id)
        } else {
            selectedNotes.add(id)
        }
    }

    private class NoteDiffCallback : DiffUtil.ItemCallback<StudyMaterials>() {
        override fun areItemsTheSame(oldItem: StudyMaterials, newItem: StudyMaterials): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: StudyMaterials, newItem: StudyMaterials): Boolean {
            return oldItem == newItem
        }
    }
}