package coding.legaspi.tmdbclient.utils

interface MediaPlayerHelper {

    fun playMusic(tutorialExactPos: String, callback: (Boolean) -> Unit)

    fun stopMusic()

}