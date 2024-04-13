package coding.legaspi.tmdbclient.data.model.tutorial

data class TutorialStatus(
    val date: String,
    val isFinish: Boolean,
    val timestamp: String,
    val tutorial: String,
    val tutorialid: String,
    val userid: String
)