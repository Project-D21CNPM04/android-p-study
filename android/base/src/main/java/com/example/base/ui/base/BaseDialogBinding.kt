package com.example.base.ui.base

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RelativeLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.example.base.utils.TIME_SHOW_NAV_BAR
import com.example.base.utils.hideNavigationBar
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class BaseDialogBinding<V : ViewBinding> : DialogFragment() {

    open fun isFullWidth(): Boolean = true
    open fun isFullHeight(): Boolean = false
    open fun isCancelableDialog(): Boolean {
        return true
    }

    protected lateinit var binding: V
        private set
    abstract fun getLayoutBinding(inflater: LayoutInflater): V
    abstract fun updateUI(savedInstanceState: Bundle?)

    private var hideNavBarJob: Job? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val root = RelativeLayout(activity)
        root.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        val widthScreen = resources.displayMetrics.widthPixels
        val dialog = Dialog(activity as FragmentActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(root)
        dialog.setCanceledOnTouchOutside(isCancelableDialog())
        dialog.window?.let { window ->
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window.setLayout(
                if (isFullWidth()) ViewGroup.LayoutParams.MATCH_PARENT else (widthScreen * 0.8f).toInt(),
                if (isFullHeight()) ViewGroup.LayoutParams.MATCH_PARENT else ViewGroup.LayoutParams.WRAP_CONTENT,
            )
            val wic = WindowInsetsControllerCompat(window,window.decorView)
            wic.hide(WindowInsetsCompat.Type.navigationBars())
            wic.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, insets ->
                val bottomInsets = insets.systemWindowInsetBottom
                view.setPadding(0, 0, 0, -bottomInsets)
                val isNavBarVisible = insets.isVisible(WindowInsetsCompat.Type.navigationBars())
                if (isNavBarVisible) {
                    hideNavBarAfterPeriodTime(window)
                }
                insets
            }
        }

        dialog.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                onBackPressed()
                true
            } else {
                false
            }
        }

        return dialog
    }

    protected open fun onBackPressed() {
        dismiss()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = getLayoutBinding(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = isCancelableDialog()
        updateUI(savedInstanceState)
    }

    fun show(fm: FragmentManager) = apply {
        show(fm, this::class.java.canonicalName)
    }

    override fun show(manager: FragmentManager, tag: String?) {
        if (manager.findFragmentByTag(tag)?.isAdded == true) {
            return
        }
        super.show(manager, tag)
    }

    override fun dismiss() {
        runCatching { super.dismiss() }.getOrElse { runCatching { dismissAllowingStateLoss() } }
    }

    private fun hideNavBarAfterPeriodTime(window: Window) {
        hideNavBarJob?.cancel()
        hideNavBarJob = lifecycleScope.launch {
            delay(TIME_SHOW_NAV_BAR)
            window.hideNavigationBar()
        }
        hideNavBarJob?.start()
    }

    protected fun isInitialized() : Boolean{
        return this::binding.isInitialized
    }
}