package kedokato.myhoubackend.service

import kedokato.myhoubackend.config.UrlSinhVienHOU
import kedokato.myhoubackend.domain.respone.LoginResponse
import kedokato.myhoubackend.http.HttpClientFactory
import kedokato.myhoubackend.http.HttpSessionClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.net.URLEncoder

@Service
class CasLoginService(
    private val httpClientFactory: HttpClientFactory
) {
    private val casLoginUrl = UrlSinhVienHOU.CAS_LOGIN_URL
    private val logger = LoggerFactory.getLogger(CasLoginService::class.java)

    suspend fun login(username: String, password: String): LoginResponse = withContext(Dispatchers.IO) {
        val clientWithCookieStore = httpClientFactory.createClient()
        val httpClient = HttpSessionClient(clientWithCookieStore.client, clientWithCookieStore.cookieStore)


        try {
            val loginPageResponse = httpClient.get(casLoginUrl)
            val doc = Jsoup.parse(loginPageResponse.body)

            val execution = doc.select("input[name=execution]").first()?.attr("value")
                ?: throw IllegalStateException("Không lấy được execution token")

            val body = buildString {
                append("username=${URLEncoder.encode(username, "UTF-8")}")
                append("&password=${URLEncoder.encode(password, "UTF-8")}")
                append("&execution=${URLEncoder.encode(execution, "UTF-8")}")
                append("&_eventId=submit")
                append("&geolocation=")
            }

            val loginResponse = httpClient.post(casLoginUrl, body)

            val ticketUrl = loginResponse.headers["Location"]?.firstOrNull()
                ?: throw IllegalStateException("Đăng nhập thất bại - không có redirect")


            val finalResponse = httpClient.get(ticketUrl)

            val authCookie = httpClient.getCookies()[".ASPXAUTH"]
            val sessionId = httpClient.getCookies()["ASP.NET_SessionId"]


            LoginResponse(
                success = true,
                message = "Đăng nhập thành công",
                authCookie = authCookie,
                sessionId = sessionId
            )

        } catch (e: Exception) {
            logger.error("Lỗi khi đăng nhập: {}", e.message)
            LoginResponse(
                success = false,
                message = e.message ?: "Lỗi không xác định",
                authCookie = null,
                sessionId = null
            )
        } finally {
            httpClient.close()
        }
    }
}