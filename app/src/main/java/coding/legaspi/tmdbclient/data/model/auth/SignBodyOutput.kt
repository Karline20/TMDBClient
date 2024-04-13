package coding.legaspi.tmdbclient.data.model.auth

data class SignBodyOutput(
    val id: String,
    val email: String,
    val username: String,
    val emailVerified: Boolean
)
