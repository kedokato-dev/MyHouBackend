package kedokato.myhoubackend.helper

import kedokato.myhoubackend.domain.respone.ApiListResponse
import kedokato.myhoubackend.domain.respone.ApiResponse
import org.springframework.aot.hint.TypeReference.listOf
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

object ResponseHelper {
    fun <T> success(message: String = "Success", data: T): ResponseEntity<ApiResponse<T>> {
        val response = ApiResponse(
            status = HttpStatus.OK.value(),
            success = true,
            message = message,
            data = data
        )
        return ResponseEntity.ok(response)
    }

    fun <T> success(message: String = "Success", data: List<T>): ResponseEntity<ApiListResponse<List<T>>> {
        val response = ApiListResponse(
            status = HttpStatus.OK.value(),
            success = true,
            message = message,
            data = listOf(data)
        )
        return ResponseEntity.ok(response)
    }



    fun <T> created(message: String = "Created", data: T): ResponseEntity<ApiResponse<T>> {
        val response = ApiResponse(
            status = HttpStatus.CREATED.value(),
            success = true,
            message = message,
            data = data
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    fun <T> error(status: HttpStatus, message: String): ResponseEntity<ApiResponse<T>> {
        val response = ApiResponse<T>(
            status = status.value(),
            success = false,
            message = message,
            data = null
        )
        return ResponseEntity.status(status).body(response)
    }

    fun <T> errorListResponse(status: HttpStatus, message: String): ResponseEntity<ApiListResponse<T>> {
        val response = ApiListResponse<T>(
            status = status.value(),
            success = false,
            message = message,
            data = emptyList()
        )
        return ResponseEntity.status(status).body(response)
    }
}

