package com.example.base.permission.manager.impl

import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.base.permission.manager.PermissionManager

class MultiplePermissionManager(
    private val activity: AppCompatActivity,
    private val permissions: Array<String>
) : PermissionManager(activity) {

    companion object {
        const val TAG = "MultiplePermission"
    }

    private var requestMultiplePermission: ActivityResultLauncher<Array<String>> =
        activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            Log.d(TAG,"registerForActivityResult success")
            notifyResultPermission()
        }

    override fun requestPermission(requestCode: Int) {
        super.requestPermission(requestCode)
        Log.d(TAG,"registerForActivityResult launch")
        requestMultiplePermission.launch(permissions)
    }

    override fun isPermissionGranted(): Boolean {
        return permissions.none {
            ContextCompat.checkSelfPermission(
                activity,
                it
            ) != PackageManager.PERMISSION_GRANTED
        }
    }
}
