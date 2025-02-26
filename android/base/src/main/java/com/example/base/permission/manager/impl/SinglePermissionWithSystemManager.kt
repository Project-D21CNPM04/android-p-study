package com.example.base.permission.manager.impl

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
import com.example.base.permission.manager.PermissionManager

class SinglePermissionWithSystemManager : PermissionManager {
    private var activity: AppCompatActivity? = null
    private var fragment: Fragment? = null
    private var permission: String
    private val permissionCache
        get() = getContext().getSharedPreferences(
            "permission_cache",
            Context.MODE_PRIVATE
        )

    constructor(activity: AppCompatActivity, permission: String) : super(activity) {
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

    constructor(fragment: Fragment, permission: String) : super(fragment) {
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

    fun getContext(): Context {
        return activity ?: fragment?.context ?: throw IllegalAccessException()
    }

    private var requestSinglePermission: ActivityResultLauncher<String>
    private var requestSystemPermission: ActivityResultLauncher<Intent>

    override fun requestPermission(requestCode: Int) {
        super.requestPermission(requestCode)
        val firstTimeRequestPermission = permissionCache.contains(permission).not()
        if (firstTimeRequestPermission || shouldShowRationale()) {
            if (firstTimeRequestPermission) {
                permissionCache.edit { putBoolean(permission, true) }
            }
            requestSinglePermission.launch(permission)
        } else {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", getContext().packageName, null)
            intent.data = uri
            requestSystemPermission.launch(intent)
        }
    }

    private fun shouldShowRationale(): Boolean {
        return activity?.shouldShowRequestPermissionRationale(permission)
            ?: fragment?.shouldShowRequestPermissionRationale(permission)
            ?: throw IllegalAccessException()
    }

    override fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            getContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}
