package coding.legaspi.tmdbclient.utils

import android.content.Context

object MediaPlayerFactory {
    fun create(context: Context): MediaPlayerHelper = MediaPlayerHelperImpl(context)

}