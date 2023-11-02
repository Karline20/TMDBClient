package coding.legaspi.tmdbclient.data.model.ranking

data class Ranking(
    val date: String,
    val name: String,
    val score: Int,
    val timestamp: String,
    val userid: String
)