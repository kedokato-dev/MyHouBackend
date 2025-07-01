package kedokato.myhoubackend.service

import kedokato.myhoubackend.config.UrlSinhVienHOU
import kedokato.myhoubackend.http.HttpClientFactory
import kedokato.myhoubackend.http.HttpSessionClient
import kedokato.myhoubackend.model.Student
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController
import org.springframework.stereotype.Service

@Service
class StudentInfoService(
    private val httpClientFactory: HttpClientFactory,
    private val basicErrorController: BasicErrorController
) {
    private val logger = LoggerFactory.getLogger(StudentInfoService::class.java)

    suspend fun getStudentInfo(authCookie: String, sessionId: String): Student? = withContext(Dispatchers.IO) {
        val clientWithCookieStore = httpClientFactory.createClient()
        val httpClient = HttpSessionClient(clientWithCookieStore.client, clientWithCookieStore.cookieStore)

        try {
            logger.info("Setting cookies - AuthCookie: {}, SessionId: {}", authCookie, sessionId)

            // Set cookies with proper domain and path
            httpClient.setCookieWithDomain(".ASPXAUTH", authCookie, UrlSinhVienHOU.HOU_BASE_URL, "/")
            httpClient.setCookieWithDomain("ASP.NET_SessionId", sessionId, UrlSinhVienHOU.HOU_BASE_URL, "/")

            logger.info("Current cookies: {}", httpClient.getCookies())

            val response = httpClient.get(UrlSinhVienHOU.HO_SO_SINH_VIEN)

            logger.info("Response status: {}, Body length: {}", response.body.length)

            // Check for redirect
            if (response.body.contains("Object moved") || response.headers.containsKey("Location")) {
                val locationHeader = response.headers["Location"]?.firstOrNull()
                logger.warn("Redirect detected to: {}", locationHeader)

                if (locationHeader != null) {
                    val absoluteUrl = if (locationHeader.startsWith("http")) {
                        locationHeader
                    } else {
                        "${UrlSinhVienHOU.HOU_BASE_URL}$locationHeader"
                    }

                    logger.info("Following redirect to: {}", absoluteUrl)

                    // Check if redirecting to login page
                    if (absoluteUrl.contains("Login.aspx") || absoluteUrl.contains("cas/login")) {
                        logger.warn("Redirected to login page - authentication failed")
                        return@withContext null
                    }

                    val redirectResponse = httpClient.get(absoluteUrl)
                    val doc = Jsoup.parse(redirectResponse.body)
                    return@withContext parseStudentInfo(doc)
                }
                return@withContext null
            }

            val doc = Jsoup.parse(response.body)
            logger.info("Document title: {}", doc.title())

            return@withContext parseStudentInfo(doc)

        } catch (e: Exception) {
            logger.error("Error getting student info: {}", e.message, e)
            null
        } finally {
            httpClient.close()
        }
    }

    private fun parseStudentInfo(doc: org.jsoup.nodes.Document): Student {
        // Extract student information from the page
        val studentId = doc.select("#txtMa_sv")?.attr("value")
        val studentName = doc.select("#txtHo_ten")?.attr("value")
        val birthDate = doc.select("#txtNgay_sinh")?.attr("value")
        val sex = doc.select("#txtGioi_tinh")?.attr("value") ?: "Không xác định"
        val address = doc.select("#txtNoi_sinh")?.attr("value")
        val phoneParent = doc.select("#txtDien_thoai_nr")?.attr("value")
        val phone = doc.select("#txtDien_thoai_cn")?.attr("value")
        val email = doc.select("#txtEmail")?.attr("value")
        val addressNow = doc.select("#txtNoi_o_hien_nay")?.attr("value")

        val programType = doc.select("#txtTen_he")?.attr("value")
        val faculty = doc.select("#txtTen_khoa")?.attr("value")
        val courseYear = doc.select("#txtKhoa_hoc")?.attr("value")
        val major = doc.select("#txtTen_chuyen_nganh")?.attr("value")
        val className = doc.select("#txtTen_lop")?.attr("value")

        return Student(
            id = studentId,
            name = studentName,
            birthDate = birthDate,
            sex = sex,
            address = address,
            phoneParent = phoneParent,
            phone = phone,
            email = email,
            addressNow = addressNow,
            programType = programType,
            faculty = faculty,
            courseYear = courseYear,
            major = major,
            className = className
        )
    }
}