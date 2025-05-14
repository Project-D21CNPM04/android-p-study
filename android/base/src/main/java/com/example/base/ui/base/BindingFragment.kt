package com.example.base.ui.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.concurrent.atomic.AtomicBoolean


abstract class BindingFragment<V : ViewBinding> : Fragment() {

    protected lateinit var binding: V

    private val _lifecycleEventState: MutableStateFlow<Lifecycle.Event> =
        MutableStateFlow(Lifecycle.Event.ON_ANY)

    protected val lifecycleEventState get() = _lifecycleEventState.asStateFlow()

    private val hasInitView = AtomicBoolean(false)
    protected abstract fun inflateBinding(inflater: LayoutInflater): V
    abstract fun updateUI(view: View, savedInstanceState: Bundle?)

    @ColorRes
    open fun getStatusBarColor(): Int {
        return android.R.color.white
    }

    open fun isFullScreen(): Boolean {
        return false
    }

    open fun onViewInitialized() {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflateBinding(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addFragmentLifecycleEvent()
        updateUI(view, savedInstanceState)
        if (hasInitView.compareAndSet(false, true)) {
            onViewInitialized()
        }
    }

    private fun addFragmentLifecycleEvent() {
        viewLifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                _lifecycleEventState.update { event }
                if (event == Lifecycle.Event.ON_DESTROY) {
                    viewLifecycleOwner.lifecycle.removeObserver(this)
                }
            }
        })
    }

    fun setStatusBarColor(@ColorInt colorInt: Int) {
        val activity = activity
        if (activity is KoreActivity) {
            activity.setStatusBarColor(colorInt)
        } else {
            activity?.window?.statusBarColor = colorInt
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

    fun getContextF(): Context {
        return context ?: activity ?: throw IllegalAccessException()
    }

    fun bindingInitialized(): Boolean {
        return this::binding.isInitialized
    }
}