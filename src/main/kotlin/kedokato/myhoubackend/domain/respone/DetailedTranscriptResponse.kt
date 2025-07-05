package kedokato.myhoubackend.domain.respone

data class DetailedTranscriptResponse (
    val semester: String? = "",
    val courseYear: String?= "",
    val subjectId: String? = "",
    val subjectName: String? = "",
    val credits: Int? = -1,
    val grade10: Double? = -1.0,
    val grade4: Double? = -1.0,
    val letterGrade: String? = "",
    val isIncludedInGPA: Boolean? = false,
    val note: String? = "",
    val detailScoreUrl: String? = "",
    val subjectGradeDetails: List<SubjectGradeDetails> = emptyList()
)


data class SubjectGradeDetails(
    val studyAttempt: Int? = null,
    val examAttempt: Int? = null,
    val componentScore: String? = "",
    val examIneligibilityReason: String? = "",
    val practicalExamScore: Double? = null,
    val examScore: Double? = null,
    val bonusPoint: Double? = null,
    val note: String? = "",
)