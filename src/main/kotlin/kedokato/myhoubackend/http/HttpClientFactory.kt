package kedokato.myhoubackend.http

import org.apache.http.client.CookieStore
import org.apache.http.client.config.RequestConfig
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.springframework.stereotype.Component

@Component
class HttpClientFactory {

    data class HttpClientWithCookieStore(
        val client: CloseableHttpClient,
        val cookieStore: CookieStore
    )

    fun createClient(): HttpClientWithCookieStore {
        val cookieStore = BasicCookieStore()
        val client = HttpClients.custom()
            .setDefaultCookieStore(cookieStore)
            .setDefaultRequestConfig(
                RequestConfig.custom()
                    .setRedirectsEnabled(false)
                    .build()
            )
            .build()

        return HttpClientWithCookieStore(client, cookieStore)
    }
}