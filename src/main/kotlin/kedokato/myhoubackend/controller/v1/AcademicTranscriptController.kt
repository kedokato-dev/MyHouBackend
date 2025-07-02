package kedokato.myhoubackend.controller.v1

import kedokato.myhoubackend.domain.respone.DetailedTranscriptResponse
import kedokato.myhoubackend.domain.respone.GeneralTranscriptResponse
import kedokato.myhoubackend.service.AcademicTranscriptService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("v1/api")
class AcademicTranscriptController(
    private val academicTranscriptService: AcademicTranscriptService
) {
    // Fix: Use correct class reference
    private val logger = LoggerFactory.getLogger(AcademicTranscriptController::class.java)

    @PostMapping("/academic-transcript")
    suspend fun getAcademicTranscript(
        @RequestHeader("Auth-Cookie") authCookie: String,
        @RequestHeader("Session-Id") sessionId: String
    ): List<DetailedTranscriptResponse> {
        logger.info("Received request with AuthCookie length: {}, SessionId length: {}",
            authCookie.length, sessionId.length)
        return try {
            academicTranscriptService.getAcademicTranscript(authCookie, sessionId)
        } catch (e: Exception) {
            logger.error("Lỗi khi lấy thông tin bảng điểm: {}", e.message)
            emptyList()
        }
    }

    @PostMapping("/genaral-transcript")
    suspend fun getGeneralTranscript(
        @RequestHeader("Auth-Cookie") authCookie: String,
        @RequestHeader("Session-Id") sessionId: String
    ): GeneralTranscriptResponse {
        logger.info("Received request for general transcript with AuthCookie length: {}, SessionId length: {}",
            authCookie.length, sessionId.length)
        return try {
            academicTranscriptService.getGeneralTranscript(authCookie, sessionId)
        } catch (e: Exception) {
            logger.error("Lỗi khi lấy thông tin bảng điểm tổng quát: {}", e.message)
            GeneralTranscriptResponse() // Return an empty response or handle as needed
        }
    }
}