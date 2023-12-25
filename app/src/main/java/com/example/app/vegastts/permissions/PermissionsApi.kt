package com.rhonda.app.vegasrhonda.permissions

import android.app.Activity

interface PermissionsApi {

    fun hasAllPermissions(activity: Activity): Boolean

    fun hasBasePermissions(activity: Activity): Boolean
    fun requestBasePermissions(activity: Activity)

}