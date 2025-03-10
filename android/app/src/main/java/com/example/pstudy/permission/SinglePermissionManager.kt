package com.example.pstudy.permission

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.Fragment

class SinglePermissionManager : PermissionManager {

    companion object {
        private const val PERMISSION_REQUEST_TIME_TO_NOT_SHOW_SYSTEM_POPUP = 2
    }
    private var activity: AppCompatActivity? = null
    private var fragment: Fragment? = null
    private var permission: String

    constructor(activity: AppCompatActivity, permission: String) : super() {
        this.activity = activity
        this.permission = permission
        requestSinglePermission =
            activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                notifyResultPermission()
            }
        requestSystemPermission =
            activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                notifyResultPermission()
            }
    }

    constructor(fragment: Fragment, permission: String) : super() {
        this.fragment = fragment
        this.permission = permission
        requestSinglePermission =
            fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                notifyResultPermission()
            }
        requestSystemPermission =
            fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                notifyResultPermission()
            }
    }

    private val permissionCache
        get() = getContext().getSharedPreferences(
            "permission_cache",
            Context.MODE_PRIVATE
        )

    private var systemPermissionDeniedTimes: Int
        set(value) {
            permissionCache.edit {
                putInt(
                    "DENIED_$permission",
                    value.coerceAtMost(PERMISSION_REQUEST_TIME_TO_NOT_SHOW_SYSTEM_POPUP)
                )
            }
        }
        get() = permissionCache.getInt("DENIED_$permission", 0)

    private fun getContext(): Context {
        return activity ?: fragment?.context ?: throw IllegalAccessException()
    }

    private var requestSystemPermission: ActivityResultLauncher<Intent>

    private var requestSinglePermission: ActivityResultLauncher<String>

    override fun requestPermission() {
        if (needPopupRequestPermission()) {
            notifyResultPermission()
        } else {
            requestSinglePermission.launch(permission)
        }
    }

    fun needPopupRequestPermission(): Boolean {
        return systemPermissionDeniedTimes == PERMISSION_REQUEST_TIME_TO_NOT_SHOW_SYSTEM_POPUP &&
                !isPermissionGranted()
    }

    override fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            getContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun shouldShowRationale(): Boolean {
        return activity?.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            ?: false
    }

    override fun notifyResultPermission() {
        super.notifyResultPermission()
        if (!isPermissionGranted()) systemPermissionDeniedTimes++
    }

    fun requestSystemPermission(needAutoDirection: Boolean = false) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", getContext().packageName, null)
        intent.data = uri
        requestSystemPermission.launch(intent)
    }
}
