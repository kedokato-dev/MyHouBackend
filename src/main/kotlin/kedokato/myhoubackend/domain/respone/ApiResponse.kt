package kedokato.myhoubackend.domain.respone


data class ApiResponse<T>(
    val status: Int,
    val success: Boolean,
    val message: String,
    val data: T? = null
)

data class ApiListResponse<T>(
    val status: Int,
    val success: Boolean = false,
    val message: String,
    val data: List<T> = emptyList()
)
