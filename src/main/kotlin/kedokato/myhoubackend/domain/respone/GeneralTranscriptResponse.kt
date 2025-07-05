package kedokato.myhoubackend.domain.respone


data class GeneralTranscriptResponse (
    val gpa4: Double? = null,
    val gpa10: Double? = null,
    val totalCredits: Int? = null,
    val ranking: String? = null,
    val retakeSubjectsCount: Int? = null,
    val reExamSubjectCount : Int? = null,
    val pendingGradesCount: Int? = null,
)
