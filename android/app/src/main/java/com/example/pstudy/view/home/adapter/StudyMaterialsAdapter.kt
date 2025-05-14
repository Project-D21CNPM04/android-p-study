package com.example.pstudy.view.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pstudy.data.model.Folder
import com.example.pstudy.data.model.StudyMaterials
import com.example.pstudy.databinding.ItemStudyMaterialBinding
import java.util.Calendar

class StudyMaterialsAdapter(
    private val onItemClick: (StudyMaterials) -> Unit,
    private val onAddToFolderClick: (StudyMaterials) -> Unit = {}
) : ListAdapter<StudyMaterialsWithFolder, StudyMaterialsAdapter.ViewHolder>(
    StudyMaterialsDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemStudyMaterialBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun submitListWithFolders(
        studyMaterials: List<StudyMaterials>,
        folderMap: Map<String, Folder>
    ) {
        val itemsWithFolders = studyMaterials.map { material ->
            StudyMaterialsWithFolder(
                studyMaterial = material,
                folder = material.folderId?.let { folderMap[it] }
            )
        }
        submitList(itemsWithFolders)
    }

    inner class ViewHolder(private val binding: ItemStudyMaterialBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StudyMaterialsWithFolder) {
            val studyMaterial = item.studyMaterial
            binding.apply {
                tvTitle.text = studyMaterial.title
                tvDate.text = formatDate(studyMaterial.timeStamp)
                tvType.text = studyMaterial.type.name.uppercase()

                if (item.folder != null) {
                    tvFolder.visibility = View.VISIBLE
                    tvFolder.text = item.folder.name
                    btnAddToFolder.setImageResource(android.R.drawable.ic_menu_edit)
                } else {
                    tvFolder.visibility = View.GONE
                    btnAddToFolder.setImageResource(android.R.drawable.ic_input_add)
                }

                root.setOnClickListener {
                    onItemClick(studyMaterial)
                }

                btnAddToFolder.setOnClickListener {
                    onAddToFolderClick(studyMaterial)
                }
            }
        }

        private fun formatDate(timestamp: Long): String {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timestamp

            val dayOfWeek = when (calendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY -> "Th 2"
                Calendar.TUESDAY -> "Th 3"
                Calendar.WEDNESDAY -> "Th 4"
                Calendar.THURSDAY -> "Th 5"
                Calendar.FRIDAY -> "Th 6"
                Calendar.SATURDAY -> "Th 7"
                Calendar.SUNDAY -> "CN"
                else -> ""
            }

            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH) + 1

            return "$dayOfWeek, $day thg $month"
        }
    }
}

data class StudyMaterialsWithFolder(
    val studyMaterial: StudyMaterials,
    val folder: Folder? = null
)

class StudyMaterialsDiffCallback : DiffUtil.ItemCallback<StudyMaterialsWithFolder>() {
    override fun areItemsTheSame(
        oldItem: StudyMaterialsWithFolder,
        newItem: StudyMaterialsWithFolder
    ): Boolean {
        return oldItem.studyMaterial.id == newItem.studyMaterial.id
    }

    override fun areContentsTheSame(
        oldItem: StudyMaterialsWithFolder,
        newItem: StudyMaterialsWithFolder
    ): Boolean {
        return oldItem.studyMaterial == newItem.studyMaterial && oldItem.folder?.id == newItem.folder?.id
    }
}