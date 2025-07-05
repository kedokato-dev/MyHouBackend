package kedokato.myhoubackend.utils

import kedokato.myhoubackend.domain.respone.ApiResponse
import kedokato.myhoubackend.helper.ResponseHelper
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(ex: NoSuchElementException): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseHelper.error(HttpStatus.NOT_FOUND, ex.message ?: "Resource not found")
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(ex: IllegalArgumentException): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseHelper.error(HttpStatus.BAD_REQUEST, ex.message ?: "Invalid request")
    }

    @ExceptionHandler(Exception::class)
    fun handleInternalError(ex: Exception): ResponseEntity<ApiResponse<Nothing>> {
        ex.printStackTrace()
        return ResponseHelper.error(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred")
    }
}
