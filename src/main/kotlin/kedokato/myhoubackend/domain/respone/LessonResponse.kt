package kedokato.myhoubackend.domain.respone



data class LessonResponse(
    val day: String,
    val session: String,
    val lessons: List<LessonDetail>
)

//data class LessonResponse(
//    val lessonsByDayAndSession: List<LessonByDayAndSession>
//)


data class LessonDetail(
    val subject: String,
    val period: String,
    val classCode: String,
    val teacher: String,
    val room: String,
    val method: String,
    val type: String
)

