package kedokato.myhoubackend.domain.respone



data class LessonByDayAndSession(
    val day: String,           // VD: "Thứ 2, 21/04/2025"
    val session: String,       // VD: "Sáng", "Chiều", "Tối"
    val lessons: List<LessonDetailResponse>
)


data class LessonDetailResponse(
    val subject: String,
    val period: String,
    val classCode: String,
    val teacher: String,
    val room: String,
    val method: String,
    val type: String
)

