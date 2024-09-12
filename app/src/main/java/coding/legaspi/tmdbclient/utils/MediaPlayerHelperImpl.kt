package coding.legaspi.tmdbclient.utils

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import coding.legaspi.tmdbclient.R

class MediaPlayerHelperImpl(private val context: Context): MediaPlayerHelper {


    private var mediaPlayer: MediaPlayer? = null

    override fun playMusic(tutorialExactPos: String, callback: (Boolean) -> Unit) {

        mediaPlayer?.release()
        mediaPlayer = MediaPlayer()
        try {
            val resourceUri: Uri = when (tutorialExactPos) {
                "Tagalog Hymn"-> Uri.parse("android.resource://${context.packageName}/${R.raw.tagalog_hymn}")
                "Chabacano Hymn"-> Uri.parse("android.resource://${context.packageName}/${R.raw.chabacano_hymn}")
                else -> throw IllegalArgumentException("Invalid tutorialExactPos: $tutorialExactPos")
            }
            mediaPlayer?.setDataSource(context, resourceUri)
            mediaPlayer?.prepare()
            mediaPlayer?.start()

            mediaPlayer?.setOnCompletionListener {
                callback(true)
            }

        } catch (e: Exception) {
            Log.e("MediaPlayerHelperImpl", "$e")
        }
    }

    override fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.reset()
    }

}