package com.example.base.model

import android.content.Context
import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize

sealed class UiText:Parcelable {

    abstract fun getBy(context: Context): CharSequence

    abstract fun getResId() : Int?

    @Parcelize
    data class Dynamic(private val value: String) : UiText() {
        override fun getBy(context: Context): String = value
        override fun getResId(): Int? = null
    }
    @Parcelize
    data class StringResource(@StringRes private val resId: Int) : UiText() {
        override fun getBy(context: Context): String {
            return context.getString(resId)
        }

        override fun getResId(): Int = resId
    }
    @Parcelize
    class NestedStringResource(
        @StringRes private val resId: Int,
        private vararg val args: UiText,
    ) : UiText() {
        override fun getBy(context: Context): String {
            return context.getString(resId, *args.map { it.getBy(context) }.toTypedArray())
        }

        override fun getResId(): Int = resId
    }

    @Parcelize
    data class AnnotateStringResource(val charSequence: CharSequence): UiText() {
        override fun getBy(context: Context): CharSequence {
            return charSequence
        }

        override fun getResId(): Int? = null
    }

    companion object {
        @JvmStatic
        fun from(value: String) = Dynamic(value)
        @JvmStatic
        fun from(@StringRes resId: Int) = StringResource(resId)
        @JvmStatic
        fun from(@StringRes resId: Int, vararg args: UiText) = NestedStringResource(resId, *args)
        @JvmStatic
        fun from(charSequence: CharSequence) = AnnotateStringResource(charSequence)
    }

}