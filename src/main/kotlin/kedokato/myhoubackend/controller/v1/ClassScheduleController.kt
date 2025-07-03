package kedokato.myhoubackend.controller.v1

import kedokato.myhoubackend.domain.respone.LessonByDayAndSession
import kedokato.myhoubackend.service.ClassScheduleService
import kedokato.myhoubackend.utils.DateUtils
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

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
    ): List<LessonByDayAndSession> {
        return try {
            classScheduleService.getClassScheduleByWeek(authCookie, sessionId, dateUtils.getWeek(weekOffset))
        } catch (e: Exception) {
            logger.error("Lỗi khi lấy thông tin bảng điểm: {}", e.message)
            emptyList()
        }
    }


    @PostMapping("/class-schedule/month")
    suspend fun getClassScheduleByMonth(
        @RequestHeader("Auth-Cookie") authCookie: String,
        @RequestHeader("Session-Id") sessionId: String,
        @RequestParam("monthOffset", defaultValue = "0") monthOffset: Int
    ): List<LessonByDayAndSession> {
        return try {
            classScheduleService.getClassScheduleByMonth(authCookie, sessionId, dateUtils.getMonth(monthOffset))
        } catch (e: Exception) {
            logger.error("Lỗi khi lấy thông tin bảng điểm: {}", e.message)
            emptyList()
        }
    }






}