package com.example.base.permission

interface PermissionResultInvoke {
    fun onPermissionGranted(requestCode: Int?, isGranted: Boolean)
    fun isReplayValue(): Boolean = false
}