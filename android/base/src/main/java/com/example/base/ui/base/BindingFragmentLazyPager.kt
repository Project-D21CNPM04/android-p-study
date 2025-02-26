package com.example.base.ui.base

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

abstract class BindingFragmentLazyPager<V : ViewBinding> : BindingFragment<V>() {
    private var isUpdateUI = AtomicBoolean(false)
    private var isLazyUpdateUi = AtomicBoolean(false)
    private val stateUpdateUi: MutableStateFlow<Boolean> = MutableStateFlow(false)
    final override fun updateUI(view: View, savedInstanceState: Bundle?) {
        if (isLazyUpdateUi.get().not()) {
            updateUiIfNeed(true)
        }
        stateUpdateUi.onEach {
            if (it) {
                lifecycleScope.launch(Dispatchers.Main) {
                    onFragmentSelected()
                    updateUI()
                }
            }
        }.launchIn(lifecycleScope)
    }

    fun invokeLazyPager() {
        isLazyUpdateUi.compareAndSet(false, true)
    }

    internal fun updateUiIfNeed(isDefaultTab: Boolean) {
        if (isUpdateUI.compareAndSet(false, true)) {
            Log.d("updateUiIfNeed", "from:${this::class.java.simpleName}+${this.arguments}")
            if (isDefaultTab) {
                stateUpdateUi.update { true }
            } else {
                lifecycleScope.launchWhenResumed {
                    updateUI()
                }
            }
        }
    }
    protected abstract fun updateUI()

    open fun onFragmentSelected() {}
    open fun onFragmentUnSelected() {}
}
