package com.example.base.ui.base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.example.base.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.base.utils.TIME_SHOW_NAV_BAR
import com.example.base.utils.hideNavigationBar
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


abstract class BaseBottomSheet<T : ViewBinding> : BottomSheetDialogFragment() {
    lateinit var binding: T
        private set

    private var hideNavBarJob: Job? = null

    abstract fun inflateBinding(layoutInflater: LayoutInflater): T
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflateBinding(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogStyle)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.let { window ->
            val wic = WindowInsetsControllerCompat(window, window.decorView)
            wic.hide(WindowInsetsCompat.Type.navigationBars())
            wic.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, insets ->
                val bottomInsets = insets.getInsets(WindowInsetsCompat.Type.systemGestures()).bottom
                val isSoftInputShow = insets.isVisible(WindowInsetsCompat.Type.ime())
                val isNavBarVisible = insets.isVisible(WindowInsetsCompat.Type.navigationBars())
                if (isNavBarVisible) {
                    hideNavBarAfterPeriodTime(window)
                }
                if (!isSoftInputShow) {
                    view.setPadding(0, 0, 0, -bottomInsets)
                } else {
                    view.setPadding(0, 0, 0, 0)
                }
                insets
            }
        }
        dialog.setOnShowListener {
            dialog.findViewById<View?>(com.google.android.material.R.id.design_bottom_sheet)
                ?.let { container ->
                    container.post {
                        if (container is FrameLayout) {
                            BottomSheetBehavior.from(container).apply {
                                state = BottomSheetBehavior.STATE_EXPANDED
                            }
                        }
                    }
                }
        }
        return dialog
    }

    private fun hideNavBarAfterPeriodTime(window: Window) {
        hideNavBarJob?.cancel()
        hideNavBarJob = lifecycleScope.launch {
            delay(TIME_SHOW_NAV_BAR)
            window.hideNavigationBar()
        }
        hideNavBarJob?.start()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI()
    }

    fun show(fm: FragmentManager) {
        this.show(fm, this::class.java.canonicalName)
    }

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            super.show(manager, tag)
        } catch (e: Exception) {
            manager.beginTransaction().add(this@BaseBottomSheet, tag).commitAllowingStateLoss()
        }
    }

    fun showMessage(message: String) {
        val activity = activity
        if (activity is KoreActivity) {
            activity.showMessage(message)
        } else {
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
        }
    }

    abstract fun updateUI()
}