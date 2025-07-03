package kedokato.myhoubackend.parser

import kedokato.myhoubackend.domain.respone.LessonByDayAndSession
import kedokato.myhoubackend.domain.respone.LessonDetailResponse
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ClassScheduleParse{

    private val  logger = LoggerFactory.getLogger(ClassScheduleParse::class.java)

    suspend fun getClassSchedule(doc: Document): List<LessonByDayAndSession> {
        val rows = doc.select("tbody > tr")
        if (rows.size < 2) return emptyList()

        val days = rows[0].select("th").drop(1).map { it.text().trim() }.take(7) // Chỉ lấy Thứ 2 đến Thứ 7
        val sessionsByDay = mutableMapOf<Pair<String, String>, MutableList<LessonDetailResponse>>()

        for (i in 1 until rows.size) {
            val row = rows[i]
            val tds = row.select("td")
            val session = tds[0].text().trim()

            for (j in 1..days.size) { // chỉ lấy đến Thứ 7
                val day = days[j - 1]
                val cell = tds.getOrNull(j) ?: continue
                val p = cell.selectFirst("p")

                if (p != null) {
                    val type = p.className() // hocthuong, hocbu, nghihoc
                    val lines = p.html().split("<br>").map { Jsoup.parse(it).text().trim() }

                    val subject = lines.getOrNull(0) ?: ""
                    val periodRaw = lines.find { it.startsWith("Tiết học") || it.startsWith("Tiết học bù") } ?: ""
                    val period = periodRaw.replace("Tiết học bù:", "").replace("Tiết học:", "").trim()
                    val classCode = lines.find { it.startsWith("Mã lớp:") }?.removePrefix("Mã lớp:")?.trim() ?: ""
                    val teacher = lines.find { it.startsWith("GV:") }?.removePrefix("GV:")?.trim() ?: ""
                    val room = lines.find { it.startsWith("Phòng:") }?.removePrefix("Phòng:")?.trim() ?: ""
                    val method = lines.find { it.startsWith("Hình thức học:") }?.removePrefix("Hình thức học:")?.trim() ?: ""

                    val key = Pair(day, session)
                    sessionsByDay.computeIfAbsent(key) { mutableListOf() }
                        .add(
                            LessonDetailResponse(
                                subject = subject,
                                period = period,
                                classCode = classCode,
                                teacher = teacher,
                                room = room,
                                method = method,
                                type = type
                            )
                        )
                }
            }
        }

        val allSessions = listOf("Sáng", "Chiều", "Tối")
        val result = mutableListOf<LessonByDayAndSession>()

        for (day in days) {
            for (session in allSessions) {
                val key = Pair(day, session)
                val list = sessionsByDay[key] ?: emptyList()
                result.add(LessonByDayAndSession(day = day, session = session, lessons = list))
            }
        }

        return result
    }




}