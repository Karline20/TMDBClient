package coding.legaspi.tmdbclient.data.model.auth

data class SignBody(
    val email: String,
    val password: String,
    val username: String,
    val emailVerified: Boolean
)
