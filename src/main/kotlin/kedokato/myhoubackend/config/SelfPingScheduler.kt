package kedokato.myhoubackend.config

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.net.HttpURLConnection
import java.net.URL

@Component
class SelfPingScheduler {

    private val logger = LoggerFactory.getLogger(SelfPingScheduler::class.java)

    @Scheduled(fixedRate = 300_000) // 5 minutes = 300,000 milliseconds
    fun pingMyself() {
        val healthUrl = UrlSinhVienHOU.URL_PROD + "v1/api/health"

        try {
            val url = URL(healthUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 3000
            connection.readTimeout = 3000

            val responseCode = connection.responseCode
            logger.info("Ping /health response: $responseCode")

            connection.disconnect()
        } catch (e: Exception) {
            logger.warn("Failed to ping self: ${e.message}")
        }
    }
}