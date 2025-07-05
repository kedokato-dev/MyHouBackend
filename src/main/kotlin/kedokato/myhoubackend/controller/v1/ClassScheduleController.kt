package kedokato.myhoubackend.controller.v1

import kedokato.myhoubackend.domain.respone.ApiListResponse
import kedokato.myhoubackend.domain.respone.LessonResponse
import kedokato.myhoubackend.helper.ResponseHelper
import kedokato.myhoubackend.service.ClassScheduleService
import kedokato.myhoubackend.utils.DateUtils
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("v1/api")
class ClassScheduleController(
    private val classScheduleService: ClassScheduleService
) {
    private val logger = org.slf4j.LoggerFactory.getLogger(ClassScheduleController::class.java)
    private val dateUtils = DateUtils()

    @PostMapping("/class-schedule/week")
    suspend fun getClassSchedule(
        @RequestHeader("Auth-Cookie") authCookie: String,
        @RequestHeader("Session-Id") sessionId: String,
        @RequestParam("weekOffset", defaultValue = "0") weekOffset: Int
    ): ResponseEntity<out ApiListResponse<out Any>> {
        return try {
            classScheduleService.getClassScheduleByWeek(authCookie, sessionId, dateUtils.getWeek(weekOffset))
                ?.let { data ->
                    ResponseHelper.success("Lấy thông tin lịch học thành công", data)
                } ?: ResponseHelper.errorListResponse<LessonResponse>(
                HttpStatus.BAD_REQUEST,
                "Lỗi khi lấy thông tin lịch học"
            )
        } catch (e: Exception) {
            logger.error("Lỗi khi lấy thông tin lịch học: {}", e.message)
            ResponseHelper.errorListResponse(
                HttpStatus.BAD_REQUEST,
                "Lỗi khi lấy thông tin lịch học: ${e.message}"
            )
        }
    }

    @PostMapping("/class-schedule/month")
    suspend fun getClassScheduleByMonth(
        @RequestHeader("Auth-Cookie") authCookie: String,
        @RequestHeader("Session-Id") sessionId: String,
        @RequestParam("monthOffset", defaultValue = "0") monthOffset: Int
    ): ResponseEntity<out ApiListResponse<out Any>> {
        return try {
            classScheduleService.getClassScheduleByMonth(authCookie, sessionId, dateUtils.getMonth(monthOffset))
                ?.let { data ->
                    ResponseHelper.success("Lấy thông tin lịch học theo tháng thành công", data)
                } ?: ResponseHelper.errorListResponse<LessonResponse>(
                HttpStatus.BAD_REQUEST,
                "Lỗi khi lấy thông tin lịch học theo tháng"
            )
        } catch (e: Exception) {
            logger.error("Lỗi khi lấy thông tin lịch học theo tháng: {}", e.message)
            ResponseHelper.errorListResponse<LessonResponse>(
                HttpStatus.BAD_REQUEST,
                "Lỗi khi lấy thông tin lịch học theo tháng: ${e.message}"
            )
        }
    }
}