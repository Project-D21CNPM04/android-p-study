package com.example.base.model

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

sealed class UiDrawable {
    abstract fun getDrawable(context: Context): Drawable?
    abstract fun getDrawableRes(context: Context): Int?

    data class FromDrawable(val drawable: Drawable?) : UiDrawable() {
        override fun getDrawable(context: Context): Drawable? {
            return drawable
        }

        override fun getDrawableRes(context: Context) = null
    }

    data class FromDrawableRes(@DrawableRes val drawableRes: Int) : UiDrawable() {
        override fun getDrawable(context: Context): Drawable? {
            return ContextCompat.getDrawable(context, drawableRes)
        }

        override fun getDrawableRes(context: Context) = drawableRes
    }

    companion object {
        @JvmStatic
        fun from(drawable: Drawable?): UiDrawable = FromDrawable(drawable)

        @JvmStatic
        fun from(@DrawableRes drawableRes: Int): UiDrawable = FromDrawableRes(drawableRes)
    }
}