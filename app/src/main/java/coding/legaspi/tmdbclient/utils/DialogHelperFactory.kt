package coding.legaspi.tmdbclient.utils

import android.content.Context

object DialogHelperFactory {
    fun create(context: Context): DialogHelper = DialogHelperImpl(context)
}