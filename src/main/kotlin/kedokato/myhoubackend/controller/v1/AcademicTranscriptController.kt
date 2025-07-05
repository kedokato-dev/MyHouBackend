package kedokato.myhoubackend.controller.v1

import kedokato.myhoubackend.domain.respone.ApiListResponse
import kedokato.myhoubackend.domain.respone.ApiResponse
import kedokato.myhoubackend.domain.respone.DetailedTranscriptResponse
import kedokato.myhoubackend.domain.respone.GeneralTranscriptResponse
import kedokato.myhoubackend.helper.ResponseHelper
import kedokato.myhoubackend.service.AcademicTranscriptService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.math.max

@RestController
@RequestMapping("v1/api")
class AcademicTranscriptController(
    private val academicTranscriptService: AcademicTranscriptService
) {
    private val logger = LoggerFactory.getLogger(AcademicTranscriptController::class.java)

    @PostMapping("/academic-transcript")
    suspend fun getAcademicTranscript(
        @RequestHeader("Auth-Cookie") authCookie: String,
        @RequestHeader("Session-Id") sessionId: String
    ): ResponseEntity<out ApiListResponse<List<DetailedTranscriptResponse>>?> {
        logger.info("Received request with AuthCookie length: {}, SessionId length: {}",
            authCookie.length, sessionId.length)
        return try {
            academicTranscriptService.getAcademicTranscript(authCookie, sessionId)?.let { data->
                ResponseHelper.success(
                    message = "Lấy thông tin bảng điểm thành công",
                    data = data
                )
            } ?: ResponseHelper.errorListResponse(
                status = HttpStatus.BAD_REQUEST,
                message = "Không thể lấy thông tin bảng điểm"
            )

        } catch (e: Exception) {
            logger.error("Lỗi khi lấy thông tin bảng điểm: {}", e.message)
            ResponseHelper.errorListResponse(
                status = HttpStatus.BAD_REQUEST,
                message = "Lỗi khi lấy thông tin bảng điểm: ${e.message}"
            )
        }
    }

    @PostMapping("/genaral-transcript")
    suspend fun getGeneralTranscript(
        @RequestHeader("Auth-Cookie") authCookie: String,
        @RequestHeader("Session-Id") sessionId: String
    ): ResponseEntity<out ApiResponse<GeneralTranscriptResponse?>> {
        logger.info("Received request for general transcript with AuthCookie length: {}, SessionId length: {}",
            authCookie.length, sessionId.length)
        return try {
            academicTranscriptService.getGeneralTranscript(authCookie, sessionId)?.let { data ->
                ResponseHelper.success(
                    message = "Lấy thông tin bảng điểm tổng quát thành công",
                    data = data
                )
            } ?: ResponseHelper.error(
                status = HttpStatus.BAD_REQUEST,
                message = "Không thể lấy thông tin bảng điểm tổng quát"
            )

        } catch (e: Exception) {
            logger.error("Lỗi khi lấy thông tin bảng điểm tổng quát: {}", e.message)
            return  ResponseHelper.error(
                status = HttpStatus.BAD_REQUEST,
                message = "Lỗi khi lấy thông tin bảng điểm tổng quát: ${e.message}"
            )
        }
    }
}