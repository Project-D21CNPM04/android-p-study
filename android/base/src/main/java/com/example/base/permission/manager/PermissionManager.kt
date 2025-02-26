package com.example.base.permission.manager

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.base.permission.PermissionResultInvoke
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean

abstract class PermissionManager(private val lifecycleOwner: LifecycleOwner) {
    private val invokePermissions: CopyOnWriteArrayList<PermissionResultInvoke> = CopyOnWriteArrayList()
    private val permissionState: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    private var requestCode: Int? = null

    private val lifecycleObserver = object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    permissionState.tryEmit(if (hasRequestPermission()) isPermissionGranted() else if (isPermissionGranted()) true else null)
                }

                Lifecycle.Event.ON_RESUME -> {
                    permissionState.tryEmit(if (hasRequestPermission()) isPermissionGranted() else if (isPermissionGranted()) true else null)
                }

                Lifecycle.Event.ON_DESTROY -> {
                    lifecycleOwner.lifecycle.removeObserver(this)
                }

                else -> Unit
            }
        }
    }

    init {
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
    }

    open fun requestPermission(requestCode: Int) {
        this.requestCode = requestCode
    }

    abstract fun isPermissionGranted(): Boolean

    fun registerPermissionListener(listener: PermissionResultInvoke) {
        this.invokePermissions.add(listener)
        if (listener.isReplayValue()) {
            listener.onPermissionGranted(null, isPermissionGranted())
        }
    }

    fun unregisterPermissionListener(listener: PermissionResultInvoke) {
        this.invokePermissions.remove(listener)
    }

    private val callToResumeOneTime: AtomicBoolean = AtomicBoolean(false)
    open fun notifyResultPermission() {
        callToResumeOneTime.set(true)
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                if (callToResumeOneTime.get()) {
                    Log.d(this::class.java.simpleName, "notifyResultPermission()")
                    permissionState.tryEmit(isPermissionGranted())
                    kotlin.runCatching {
                        invokePermissions.forEach { invoke ->
                            invoke.onPermissionGranted(requestCode, isPermissionGranted())
                        }
                    }
                    callToResumeOneTime.compareAndSet(true, false)
                }
            }
        }
    }

    open fun hasRequestPermission(): Boolean {
        return false
    }

    fun getPermissionState(): StateFlow<Boolean?> {
        return permissionState.asStateFlow()
    }
}