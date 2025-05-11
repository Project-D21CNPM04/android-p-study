package com.example.pstudy.view.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pstudy.data.model.StudyMaterials
import com.example.pstudy.databinding.ItemStudyMaterialBinding
import java.util.Calendar

class StudyMaterialsAdapter(
    private val onItemClick: (StudyMaterials) -> Unit
) : ListAdapter<StudyMaterials, StudyMaterialsAdapter.ViewHolder>(StudyMaterialsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemStudyMaterialBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemStudyMaterialBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StudyMaterials) {
            binding.apply {
                tvTitle.text = item.title
                tvDate.text = formatDate(item.timeStamp)
                tvType.text = item.type.name.uppercase()

                root.setOnClickListener {
                    onItemClick(item)
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

class StudyMaterialsDiffCallback : DiffUtil.ItemCallback<StudyMaterials>() {
    override fun areItemsTheSame(oldItem: StudyMaterials, newItem: StudyMaterials): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: StudyMaterials, newItem: StudyMaterials): Boolean {
        return oldItem == newItem
    }
}