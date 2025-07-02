package kedokato.myhoubackend.service

import kedokato.myhoubackend.config.UrlSinhVienHOU
import kedokato.myhoubackend.http.HttpClientFactory
import kedokato.myhoubackend.http.HttpSessionClient
import kedokato.myhoubackend.model.Student
import kedokato.myhoubackend.parser.StudentInfoParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class StudentInfoService(
    private val httpClientFactory: HttpClientFactory,
    private val redirectValidationService: RedirectValidationService,
    private val studentInfoParser: StudentInfoParser
) {
    private val logger = LoggerFactory.getLogger(StudentInfoService::class.java)

    suspend fun getStudentInfo(authCookie: String, sessionId: String): Student? = withContext(Dispatchers.IO) {
        val clientWithCookieStore = httpClientFactory.createClient()
        val httpClient = HttpSessionClient(clientWithCookieStore.client, clientWithCookieStore.cookieStore)

        try {
            logger.info("Setting cookies - AuthCookie: {}, SessionId: {}", authCookie, sessionId)

            httpClient.setCookieWithDomain(".ASPXAUTH", authCookie, UrlSinhVienHOU.DOMAIN, "/")
            httpClient.setCookieWithDomain("ASP.NET_SessionId", sessionId, UrlSinhVienHOU.DOMAIN, "/")

            logger.info("Current cookies: {}", httpClient.getCookies())

            val response = httpClient.get(UrlSinhVienHOU.HO_SO_SINH_VIEN)
            logger.info("Response status: {}, Body length: {}", response.body.length)

            val redirectResult = redirectValidationService.checkRedirect(response)

            if (redirectResult.isRedirect) {
                if (redirectResult.isLoginRedirect) {
                    return@withContext null
                }

                redirectResult.redirectUrl?.let { url ->
                    val redirectResponse = redirectValidationService.followRedirect(httpClient, url)
                        ?: return@withContext null

                    val doc = Jsoup.parse(redirectResponse.body)
                    return@withContext studentInfoParser.parseStudentInfo(doc)
                }
                return@withContext null
            }

            val doc = Jsoup.parse(response.body)
            logger.info("Document title: {}", doc.title())

            return@withContext studentInfoParser.parseStudentInfo(doc)

        } catch (e: Exception) {
            logger.error("Error getting student info: {}", e.message, e)
            null
        } finally {
            httpClient.close()
        }
    }
}