package com.example.base.ui.base

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

open class BaseViewHolder<out T : ViewBinding>(val binding: T) :
    RecyclerView.ViewHolder(binding.root) {
    val context: Context get() = itemView.context
}