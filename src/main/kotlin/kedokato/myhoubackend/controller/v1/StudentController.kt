package kedokato.myhoubackend.controller.v1

import kedokato.myhoubackend.model.Student
import kedokato.myhoubackend.service.StudentInfoService
import org.slf4j.LoggerFactory
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
    ): ResponseEntity<Student?> {
        return try {
            val studentInfo = studentInfoService.getStudentInfo(authCookie, sessionId)
            ResponseEntity.ok(studentInfo)
        } catch (e: Exception) {
            logger.error("Lỗi khi lấy thông tin sinh viên: {}", e.message)
            ResponseEntity.internalServerError().body(null)
        }
    }
}