package kedokato.myhoubackend.service

import kedokato.myhoubackend.config.UrlSinhVienHOU
import kedokato.myhoubackend.http.HttpSessionClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class RedirectValidationService {
    private val logger = LoggerFactory.getLogger(RedirectValidationService::class.java)

    data class RedirectResult(
        val isRedirect: Boolean,
        val isLoginRedirect: Boolean,
        val redirectUrl: String? = null
    )

    fun checkRedirect(response: HttpSessionClient.HttpResponse): RedirectResult {
        if (response.body.contains("Object moved") || response.headers.containsKey("Location")) {
            val locationHeader = response.headers["Location"]?.firstOrNull()
            logger.warn("Redirect detected to: {}", locationHeader)

            if (locationHeader != null) {
                val absoluteUrl = if (locationHeader.startsWith("http")) {
                    locationHeader
                } else {
                    "${UrlSinhVienHOU.DOMAIN}$locationHeader"
                }

                val isLoginRedirect = absoluteUrl.contains("Login.aspx") || absoluteUrl.contains("cas/login")

                if (isLoginRedirect) {
                    logger.warn("Redirected to login page - authentication failed")
                }

                return RedirectResult(
                    isRedirect = true,
                    isLoginRedirect = isLoginRedirect,
                    redirectUrl = absoluteUrl
                )
            }
        }

        return RedirectResult(isRedirect = false, isLoginRedirect = false)
    }

    suspend fun followRedirect(httpClient: HttpSessionClient, redirectUrl: String): HttpSessionClient.HttpResponse? {
        return try {
            logger.info("Following redirect to: {}", redirectUrl)
            httpClient.get(redirectUrl)
        } catch (e: Exception) {
            logger.error("Error following redirect: {}", e.message, e)
            null
        }
    }

    suspend fun <T> handleRedirectAndParse(
        response: HttpSessionClient.HttpResponse,
        httpClient: HttpSessionClient,
        defaultValue: T,
        parser: suspend (String) -> T
    ): T {
        val redirectResult = checkRedirect(response)

        if (redirectResult.isRedirect) {
            if (redirectResult.isLoginRedirect) {
                logger.info("Redirect: {}", redirectResult.redirectUrl)
                return defaultValue
            }

            redirectResult.redirectUrl?.let { url ->
                val redirectResponse = followRedirect(httpClient, url) ?: return defaultValue
                return parser(redirectResponse.body)
            }
            return defaultValue
        }

        return parser(response.body)
    }
}