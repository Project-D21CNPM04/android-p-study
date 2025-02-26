package com.example.base.permission.manager.impl

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.base.permission.manager.PermissionManager

class SinglePermissionManager : PermissionManager {
    private var activity: AppCompatActivity? = null
    private var fragment: Fragment? = null
    private var permission: String
    constructor(activity: AppCompatActivity, permission: String) : super(activity) {
        this.activity = activity
        this.permission = permission
        requestSinglePermission =
            activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) {
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
    }

    fun getContext(): Context {
        return activity ?: fragment?.context ?: throw IllegalAccessException()
    }

    private var requestSinglePermission: ActivityResultLauncher<String>

    override fun requestPermission(requestCode: Int) {
        super.requestPermission(requestCode)
        requestSinglePermission.launch(permission)
    }

    override fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            getContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}
