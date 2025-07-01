package kedokato.myhoubackend.domain.respone

data class LoginResponse (
    val success: Boolean,
    val message: String,
    val authCookie: String? = null,
    val sessionId: String? = null
)