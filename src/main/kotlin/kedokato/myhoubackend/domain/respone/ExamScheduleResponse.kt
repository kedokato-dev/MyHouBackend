package kedokato.myhoubackend.domain.respone

data class ExamSchedule (
    val subjectName: String? = "",
    val numberOfExam: Int,
    val examPeriod: Int,
    val examDate: String?= "",
    val examRoom: String? = "",
    val examNumber: Int? = 0,
    val examType: String? = "",
    val note: String? = ""
)


data class ExamScheduleResponse(
    val semester: String? = "",
    val examSchedules: List<ExamSchedule> = emptyList()
)