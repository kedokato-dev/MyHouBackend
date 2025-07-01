package kedokato.myhoubackend.model

data class Account(
    val username: String,
    val password: String,
    val sessionId: String? = null,
    val message: String? = null
)