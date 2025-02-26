package com.example.base.permission.manager.impl

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager

/**
 * Created by KO Huyn on 19/09/2024.
 */
abstract class StoragePermissionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == ACTION_RESULT_GRANT_STORAGE_PERMISSION) {
            onPermissionGranted(context.let { StoragePermissionManager.isPermissionGranted(context) })
        }
    }

    protected abstract fun onPermissionGranted(isGranted: Boolean)

    companion object {
        private const val ACTION_RESULT_GRANT_STORAGE_PERMISSION = "ACTION_RESULT_STORAGE_PERMISSION"

        fun sendBroadcastNotify(context: Context) {
            LocalBroadcastManager.getInstance(context)
                .sendBroadcast(Intent(ACTION_RESULT_GRANT_STORAGE_PERMISSION))
        }

        fun register(context: Context, broadcastReceiver: StoragePermissionReceiver) {
            LocalBroadcastManager.getInstance(context)
                .registerReceiver(broadcastReceiver, createIntentFilter())
        }

        fun unregister(context: Context, broadcastReceiver: StoragePermissionReceiver) {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver)
        }

        private fun createIntentFilter(): IntentFilter {
            val intentFilter = IntentFilter()
            intentFilter.addAction(ACTION_RESULT_GRANT_STORAGE_PERMISSION)
            return intentFilter
        }
    }
}