package com.example.base.utils

import android.content.Context
import android.content.Intent

internal const val TIME_SHOW_NAV_BAR = 1500L

fun Context.startActivityAsRoot(
    activity: Class<*>,
) {
    startActivity(Intent(this, activity).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    })
}