package com.example.pstudy.permission

interface PermissionResultInvoke {
    fun onPermissionGranted(isGranted: Boolean)
    fun isReplayValue(): Boolean = false
}