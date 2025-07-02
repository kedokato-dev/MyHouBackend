package kedokato.myhoubackend.parser

import kedokato.myhoubackend.domain.respone.DetailedTranscriptResponse
import kedokato.myhoubackend.domain.respone.GeneralTranscriptResponse
import kedokato.myhoubackend.domain.respone.SubjectGradeDetails
import kedokato.myhoubackend.http.HttpSessionClient
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class AcademicTranscriptParse {
    private val logger = LoggerFactory.getLogger(AcademicTranscriptParse::class.java)

    suspend fun parseGeneralTranscript(doc: Document): GeneralTranscriptResponse {
        val gpa4 = doc.select("#lblTBC_tich_luy4")?.attr("value")?.toDoubleOrNull()
        val gpa10 = doc.select("#lblTBCHT10")?.attr("value")?.toDoubleOrNull()
        val totalCredits = doc.select("#lblSo_tin_chi_tich_luy")?.attr("value")?.toIntOrNull()
        val rankGpa4 = doc.select("#lblXep_loai_hoc_tap4")?.attr("value")?: ""
        val retakeSubjectsCount = doc.select("#lblSo_mon_hoc_lai")?.attr("value")?.toIntOrNull()
        val reExamSubjectCount = doc.select("#lblSo_mon_thi_lai")?.attr("value")?.toIntOrNull()
        val pendingGradesCount = doc.select("#lblSo_mon_cho_diem")?.attr("value")?.toIntOrNull()

        return GeneralTranscriptResponse(
            gpa4 = gpa4,
            gpa10 = gpa10,
            totalCredits = totalCredits,
            ranking = rankGpa4,
            retakeSubjectsCount = retakeSubjectsCount,
            reExamSubjectCount = reExamSubjectCount,
            pendingGradesCount = pendingGradesCount
        )
    }

    suspend fun parseTranscriptTable(
        doc: Document, baseUrl: String = "https://sinhvien.hou.edu.vn/",
        httpClient: HttpSessionClient
    ): List<DetailedTranscriptResponse> = coroutineScope {
        val rows = doc.select("tr.RowStyle, tr.AltRowStyle")

        // Process all rows concurrently
        val transcriptDeferreds = rows.map { row ->
            async {
                val cells = row.select("td")
                if (cells.size >= 11) {
                    val semester = cells[0].text().trim()
                    val academicYear = cells[1].text().trim()
                    val subjectId = cells[2].text().trim()
                    val subjectName = cells[3].text().trim()
                    val credits = cells[4].text().trim().toIntOrNull() ?: 0
                    val grade10 = cells[5].text().trim().toDoubleOrNull() ?: 0.0
                    val grade4 = cells[6].text().trim().toDoubleOrNull() ?: 0.0
                    val letterGrade = cells[7].text().trim()
                    val isIncludedInGPA = !cells[8].select("input[checked]").isEmpty()
                    val note = cells[9].text().trim().takeIf { it.isNotEmpty() && it != "&nbsp;" } ?: ""
                    val detailLink = cells[10].select("a").attr("href")

                    // Parse detailed scores concurrently if link exists
                    val subjectGradeDetails = if (detailLink.isNotEmpty()) {
                        parseSubjectGradeDetails(baseUrl + detailLink, httpClient)
                    } else {
                        emptyList()
                    }

                    DetailedTranscriptResponse(
                        semester = semester,
                        courseYear = academicYear,
                        subjectId = subjectId,
                        subjectName = subjectName,
                        credits = credits,
                        grade10 = grade10,
                        grade4 = grade4,
                        letterGrade = letterGrade,
                        isIncludedInGPA = isIncludedInGPA,
                        note = note,
                        detailScoreUrl = detailLink,
                        subjectGradeDetails = subjectGradeDetails
                    )
                } else {
                    null
                }
            }
        }

        // Wait for all concurrent operations to complete and filter out nulls
        transcriptDeferreds.awaitAll().filterNotNull()
    }

    private suspend fun parseSubjectGradeDetails(detailUrl: String, httpClient: HttpSessionClient): List<SubjectGradeDetails> {
        return try {
            val response = httpClient.get(detailUrl)
            val detailDoc = Jsoup.parse(response.body)

            val detailRows = detailDoc.select("tbody tr.RowStyle, tbody tr.AltRowStyle")

            // Process detail rows concurrently
            coroutineScope {
                val detailDeferreds = detailRows.map { row ->
                    async {
                        val cells = row.select("td")
                        if (cells.size >= 12) {
                            val studyAttempt = cells[2].text().trim().toIntOrNull() ?: 0
                            val examAttempt = cells[3].text().trim().toIntOrNull() ?: 0
                            val componentScore = cells[4].text().trim().takeIf { it.isNotEmpty() && it != "&nbsp;" } ?: "Chưa có điểm"
                            val examIneligibilityReason = cells[5].text().trim().takeIf { it.isNotEmpty() && it != "&nbsp;" } ?: ""
                            val examScore = cells[6].text().trim().toDoubleOrNull()?: -1.0
                            val practicalExamScore = cells[7].text().trim().toDoubleOrNull() ?: -1.0
                            val bonusPoint = cells[8].text().trim().toDoubleOrNull() ?: 0.0
                            val note = cells[9].text().trim().takeIf { it.isNotEmpty() && it != "&nbsp;" } ?: ""

                            SubjectGradeDetails(
                                studyAttempt = studyAttempt,
                                examAttempt = examAttempt,
                                componentScore = componentScore,
                                examIneligibilityReason = examIneligibilityReason,
                                practicalExamScore = practicalExamScore,
                                examScore = examScore,
                                bonusPoint = bonusPoint,
                                note = note,
                            )
                        } else {
                            null
                        }
                    }
                }

                detailDeferreds.awaitAll().filterNotNull()
            }
        } catch (e: Exception) {
            logger.error("Error parsing subject grade details: ${e.message}", e)
            emptyList()
        }
    }
}