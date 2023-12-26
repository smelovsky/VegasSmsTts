package com.rhonda.app.vegasrhonda.permissions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.app.vegastts.mainViewModel

val basePermissions = arrayOf(
    Manifest.permission.RECEIVE_SMS,
    Manifest.permission.WAKE_LOCK,
)

data class PermissionsViewState(
    val RECEIVE_SMS: Boolean = false,
    val WAKE_LOCK: Boolean = false,

    val permissionsGranted: Boolean = false,
)

class PermissionsImpl(val context: Context): PermissionsApi {

    private fun Context.findActivity(): Activity? = when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }

    override fun hasAllPermissions(activity: Activity): Boolean{

        var result = true

        if (!hasBasePermissions(activity)) {
            result = false
        }


        mainViewModel.permissionsViewState.value =
            mainViewModel.permissionsViewState.value.copy(permissionsGranted = result)

        return result
    }

    override fun hasBasePermissions(activity: Activity): Boolean{
        var result = true
        basePermissions.forEach {

            val permission = ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            if ( !permission)
            {
                result = false
            }
            Log.d("zzz", "${it}, ${permission}")

            when (it) {

                Manifest.permission.RECEIVE_SMS -> mainViewModel.permissionsViewState.value =
                    mainViewModel.permissionsViewState.value.copy(RECEIVE_SMS = permission)

                Manifest.permission.WAKE_LOCK -> mainViewModel.permissionsViewState.value =
                    mainViewModel.permissionsViewState.value.copy(WAKE_LOCK = permission)

            }
        }

        return result
    }

    override fun requestBasePermissions(activity: Activity) {
        ActivityCompat.requestPermissions(activity, basePermissions,101)
    }

}