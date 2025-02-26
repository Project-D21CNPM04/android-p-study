package com.example.base.model

import android.content.Context
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat


sealed class UiColor {
    abstract fun getColorInt(context: Context): Int

    data class FromColorInt(@ColorInt val colorInt: Int) : UiColor() {
        override fun getColorInt(context: Context): Int {
            return colorInt
        }
    }

    data class FromColorRes(@ColorRes val colorRes: Int) : UiColor() {
        override fun getColorInt(context: Context): Int {
            return ContextCompat.getColor(context, colorRes)
        }
    }
}