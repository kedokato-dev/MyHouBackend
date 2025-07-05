package kedokato.myhoubackend.service

import kedokato.myhoubackend.config.UrlSinhVienHOU
import kedokato.myhoubackend.domain.respone.LessonResponse
import kedokato.myhoubackend.http.HttpClientFactory
import kedokato.myhoubackend.http.HttpSessionClient
import kedokato.myhoubackend.parser.ClassScheduleParse
import kedokato.myhoubackend.utils.MonthData
import kedokato.myhoubackend.utils.WeekDates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.springframework.stereotype.Service
import java.net.URLEncoder

@Service
class ClassScheduleService(
    private val classScheduleParse: ClassScheduleParse,
    private val httpClientFactory: HttpClientFactory,
    private val redirectValidationService: RedirectValidationService,
) {
    private val logger = org.slf4j.LoggerFactory.getLogger(ClassScheduleService::class.java)

    suspend fun getClassScheduleByWeek(
        authCookie: String,
        sessionId: String,
        weekDates: WeekDates
    ): List<LessonResponse>? = withContext(Dispatchers.IO) {
        val clientWithCookieStore = httpClientFactory.createClient()
        val httpClient = HttpSessionClient(clientWithCookieStore.client, clientWithCookieStore.cookieStore)

        try {
            httpClient.setCookieWithDomain(".ASPXAUTH", authCookie, UrlSinhVienHOU.DOMAIN, "/")
            httpClient.setCookieWithDomain("ASP.NET_SessionId", sessionId, UrlSinhVienHOU.DOMAIN, "/")

            val initialResponse = httpClient.get(UrlSinhVienHOU.LICH_HOC_TUAN)

            return@withContext redirectValidationService.handleRedirectAndParse(
                response = initialResponse,
                httpClient = httpClient,
                defaultValue = null
            ) { body ->
                val doc = Jsoup.parse(body)

                val viewState = doc.select("input[name=__VIEWSTATE]").first()?.attr("value") ?: ""
                val viewStateGenerator = doc.select("input[name=__VIEWSTATEGENERATOR]").first()?.attr("value") ?: ""
                val eventValidation = doc.select("input[name=__EVENTVALIDATION]").first()?.attr("value") ?: ""

                val weekApiFormat = weekDates.toApiFormat()

                val payload = buildString {
                    append("__EVENTTARGET=cmbTuan_thu")
                    append("&__EVENTARGUMENT=")
                    append("&__LASTFOCUS=")
                    append("&__VIEWSTATE=${URLEncoder.encode(viewState, "UTF-8")}")
                    append("&__VIEWSTATEGENERATOR=${URLEncoder.encode(viewStateGenerator, "UTF-8")}")
                    append("&__EVENTVALIDATION=${URLEncoder.encode(eventValidation, "UTF-8")}")
                    append("&cmbTuan_thu=${URLEncoder.encode(weekApiFormat, "UTF-8")}")
                }

                val postResponse = httpClient.post(UrlSinhVienHOU.LICH_HOC_TUAN, payload)
                val postDoc = Jsoup.parse(postResponse.body)

              classScheduleParse.getClassSchedule(postDoc)
            }

        } catch (e: Exception) {
            logger.error("Error getting class schedule: {}", e.message, e)
            return@withContext null
        } finally {
            httpClient.close()
        }
    }

    suspend fun getClassScheduleByMonth(
        authCookie: String,
        sessionId: String,
        monthData: MonthData
    ): List<LessonResponse>? = withContext(Dispatchers.IO) {
        val allSchedules = mutableListOf<LessonResponse>()

        try {
            monthData.weeks.forEach { weekDates ->
                val weekSchedule = getClassScheduleByWeek(authCookie, sessionId, weekDates)
                if (weekSchedule != null) {
                    allSchedules.addAll(weekSchedule)
                }
            }

            return@withContext allSchedules
        } catch (e: Exception) {
            logger.error("Error getting monthly class schedule: {}", e.message, e)
            return@withContext null
        }
    }
}