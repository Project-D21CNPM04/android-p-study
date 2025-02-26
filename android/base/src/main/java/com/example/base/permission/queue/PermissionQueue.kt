package com.example.base.permission.queue

import android.util.Log
import com.example.base.permission.PermissionResultInvoke
import com.example.base.permission.manager.PermissionManager

class PermissionQueue {
    data class PermissionAction(
        val permissionManager: PermissionManager,
        val nextAction: PermissionNextAction
    )

    private val queue = ArrayDeque<PermissionAction>()
    private var onFinish: (Boolean) -> Unit = {}
    fun enqueue(
        permissionManager: PermissionManager,
        nextAction: PermissionNextAction
    ) = apply {
        queue.add(PermissionAction(permissionManager, nextAction))
    }

    private val listPermissionExecuted = mutableListOf<PermissionResult>()

    fun executePermissions(onFinish: (Boolean) -> Unit) {
        this.onFinish = onFinish
        execute()
    }

    @Throws(PermissionRuntimeException::class)
    private fun execute() {
        if (queue.isEmpty()) {
            Log.d("PermissionQueue", "execute queue.isEmpty()")
            onFinish(true)
            return
        }
        val permissionAction = queue.removeFirst()
        val permissionManager = permissionAction.permissionManager
        val actionNext = permissionAction.nextAction
        val listener = object : PermissionResultInvoke {
            override fun onPermissionGranted(requestCode: Int?, isGranted: Boolean) {
                listPermissionExecuted.add(PermissionResult(isGranted))
                if (queue.isNotEmpty()) {
                    when (actionNext) {
                        PermissionNextAction.NextAtAll -> {
                            Log.d("PermissionQueue", "NextAtAll")
                            execute()
                        }

                        PermissionNextAction.NextWhenGranted -> {
                            if (isGranted) {
                                Log.d("PermissionQueue", "NextWhenGranted true")
                                execute()
                            } else {
                                Log.d("PermissionQueue", "NextWhenGranted false")
                                onFinish(false)
                            }
                        }
                    }
                } else {
                    Log.d("PermissionQueue", "onPermissionGranted queue.isEmpty()")
                    onFinish(isGranted)
                }
                permissionManager.unregisterPermissionListener(this)
            }
        }
        permissionManager.registerPermissionListener(listener)
        permissionManager.requestPermission(-1)
    }

    open class PermissionRuntimeException(message: String) : Exception(message)
    class PermissionNotAllowException(message: String) : PermissionRuntimeException(message)
}
