package kedokato.myhoubackend.controller.v1

import kedokato.myhoubackend.domain.respone.ApiResponse
import kedokato.myhoubackend.helper.ResponseHelper
import kedokato.myhoubackend.model.Student
import kedokato.myhoubackend.service.StudentInfoService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("v1/api/student")
class StudentController(
    private val studentInfoService: StudentInfoService
) {
    private val logger = LoggerFactory.getLogger(StudentController::class.java)

    @GetMapping("/info")
    suspend fun getStudentInfo(
        @RequestHeader("Auth-Cookie") authCookie: String,
        @RequestHeader("Session-Id") sessionId: String
    ): ResponseEntity<ApiResponse<Student>> {
        return try {
            studentInfoService.getStudentInfo(authCookie, sessionId)?.let { studentInfo ->
                ResponseHelper.success(data = studentInfo)
            } ?: ResponseHelper.error(HttpStatus.BAD_REQUEST, "Không thể lấy thông tin sinh viên")
        } catch (e: Exception) {
            logger.error("Lỗi khi lấy thông tin sinh viên: {}", e.message)
            ResponseHelper.error(HttpStatus.BAD_REQUEST, "Không thể lấy thông tin sinh viên")
        }
    }
}