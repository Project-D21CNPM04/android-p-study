package com.example.pstudy.permission

import android.Manifest
import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.edit

class StoragePermissionManager(private val activity: AppCompatActivity) : PermissionManager() {
    private val requestSinglePermission: ActivityResultLauncher<String> =
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            notifyResultPermission()
        }
    private val requestActivityResultContracts =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
            notifyResultPermission()
        }

    private val permissionCache
        get() = activity.getSharedPreferences(
            "permission_cache",
            Context.MODE_PRIVATE
        )

    private var systemPermissionDeniedTimes: Int
        set(value) {
            permissionCache.edit {
                putInt(
                    "DENIED_$WRITE_EXTERNAL_STORAGE_PERMISSION",
                    value.coerceAtMost(2)
                )
            }
        }
        get() = permissionCache.getInt("DENIED_$WRITE_EXTERNAL_STORAGE_PERMISSION", 0)

    override fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requestStoragePermissionApi30()
        } else {
            requestStoragePermissionApi29()
        }
    }

    fun canShowPopupDevice(): Boolean {
        return (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) && systemPermissionDeniedTimes != 2
    }

    override fun isPermissionGranted(): Boolean {
        return isPermissionGranted(activity)
    }

    override fun notifyResultPermission() {
        super.notifyResultPermission()
        if (!isPermissionGranted()) systemPermissionDeniedTimes++
    }

    private fun requestStoragePermissionApi29() {
        if (systemPermissionDeniedTimes == 2) {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", activity.packageName, null)
            intent.data = uri
            requestActivityResultContracts.launch(intent)
        } else {
            requestSinglePermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun requestStoragePermissionApi30() {
        kotlin.runCatching {
            val intent = Intent()
            intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
            val uri = Uri.fromParts("package", activity.packageName, null)
            intent.data = uri
            requestActivityResultContracts.launch(intent)
        }.onFailure {
            val intent = Intent()
            intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
            requestActivityResultContracts.launch(intent)
        }
    }

    companion object {
        private const val MANAGE_EXTERNAL_STORAGE_PERMISSION = "android:manage_external_storage"
        private const val WRITE_EXTERNAL_STORAGE_PERMISSION =
            "android.permission.WRITE_EXTERNAL_STORAGE"

        @JvmStatic
        fun isPermissionGranted(activity: Activity): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                checkStoragePermissionApi30(activity)
            } else {
                checkStoragePermissionApi29(activity)
            }
        }

        @RequiresApi(Build.VERSION_CODES.R)
        private fun checkStoragePermissionApi30(activity: Activity): Boolean {
            val appOps = activity.getSystemService(AppOpsManager::class.java)
            val mode = appOps.unsafeCheckOpNoThrow(
                MANAGE_EXTERNAL_STORAGE_PERMISSION,
                activity.applicationInfo.uid,
                activity.packageName
            )

            return mode == AppOpsManager.MODE_ALLOWED
        }

        private fun checkStoragePermissionApi29(activity: Activity): Boolean {
            val status =
                ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            return status == PackageManager.PERMISSION_GRANTED
        }
    }
}
