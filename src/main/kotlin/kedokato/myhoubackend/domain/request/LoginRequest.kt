package kedokato.myhoubackend.domain.request

data class LoginRequest (
    val username: String,
    val password: String
)