package kedokato.myhoubackend.service

import kedokato.myhoubackend.config.UrlSinhVienHOU
import kedokato.myhoubackend.domain.respone.DetailedTranscriptResponse
import kedokato.myhoubackend.domain.respone.GeneralTranscriptResponse
import kedokato.myhoubackend.http.HttpClientFactory
import kedokato.myhoubackend.http.HttpSessionClient
import kedokato.myhoubackend.parser.AcademicTranscriptParse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class AcademicTranscriptService(
    private val academicTranscriptParse: AcademicTranscriptParse,
    private val httpClientFactory: HttpClientFactory,
    private val redirectValidationService: RedirectValidationService,
) {
    private val logger = LoggerFactory.getLogger(AcademicTranscriptService::class.java)

    suspend fun getAcademicTranscript(authCookie: String, sessionId: String): List<DetailedTranscriptResponse> =
        withContext(Dispatchers.IO) {
            val clientWithCookieStore = httpClientFactory.createClient()
            val httpClient = HttpSessionClient(clientWithCookieStore.client, clientWithCookieStore.cookieStore)

            try {
                httpClient.setCookieWithDomain(".ASPXAUTH", authCookie, UrlSinhVienHOU.DOMAIN, "/")
                httpClient.setCookieWithDomain("ASP.NET_SessionId", sessionId, UrlSinhVienHOU.DOMAIN, "/")

                val response = httpClient.get(UrlSinhVienHOU.KET_QUA_HOC_TAP)

                return@withContext redirectValidationService.handleRedirectAndParse(
                    response = response,
                    httpClient = httpClient,
                    defaultValue = emptyList()
                ) { body ->
                    val doc = Jsoup.parse(body)
                    logger.info("Document title: {}", doc.title())
                    academicTranscriptParse.parseTranscriptTable(doc, UrlSinhVienHOU.HOU_BASE_URL, httpClient)
                }


            } catch (e: Exception) {
                logger.error("Error getting academic transcript: {}", e.message, e)
                return@withContext emptyList()
            }
            finally {
                httpClient.close()
            }
        }

    suspend fun getGeneralTranscript(authCookie: String, sessionId: String): GeneralTranscriptResponse =
        withContext(Dispatchers.IO) {
            val clientWithCookieStore = httpClientFactory.createClient()
            val httpClient = HttpSessionClient(clientWithCookieStore.client, clientWithCookieStore.cookieStore)

            try {

                httpClient.setCookieWithDomain(".ASPXAUTH", authCookie, UrlSinhVienHOU.DOMAIN, "/")
                httpClient.setCookieWithDomain("ASP.NET_SessionId", sessionId, UrlSinhVienHOU.DOMAIN, "/")

                val response = httpClient.get(UrlSinhVienHOU.KET_QUA_HOC_TAP)


                return@withContext redirectValidationService.handleRedirectAndParse(
                    response = response,
                    httpClient = httpClient,
                    defaultValue = GeneralTranscriptResponse()
                ) { body ->
                    val doc = Jsoup.parse(body)
                    logger.info("Document title: {}", doc.title())
                    academicTranscriptParse.parseGeneralTranscript(doc)
                }

            } catch (e: Exception) {
                logger.error("Error getting general transcript: {}", e.message, e)
                return@withContext GeneralTranscriptResponse()
            } finally {
                httpClient.close()
            }
        }
}