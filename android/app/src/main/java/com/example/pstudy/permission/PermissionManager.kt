package com.example.pstudy.permission


abstract class PermissionManager {
    private val invokePermissions: MutableList<PermissionResultInvoke> = mutableListOf()

    abstract fun requestPermission()
    abstract fun isPermissionGranted(): Boolean

    fun registerPermissionListener(listener: PermissionResultInvoke) {
        this.invokePermissions.add(listener)
        if (listener.isReplayValue()) {
            listener.onPermissionGranted(isPermissionGranted())
        }
    }

    fun unregisterPermissionListener(listener: PermissionResultInvoke) {
        this.invokePermissions.remove(listener)
    }

    open fun notifyResultPermission() {
        invokePermissions.forEach { invoke ->
            invoke.onPermissionGranted(isPermissionGranted())
        }
    }
}