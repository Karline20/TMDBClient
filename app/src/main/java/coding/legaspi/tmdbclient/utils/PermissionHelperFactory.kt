package coding.legaspi.tmdbclient.utils

import android.app.Activity

object PermissionHelperFactory {
    fun create(activity: Activity): PermissionHelper = PermissionHelper(activity)
}