package kedokato.myhoubackend.http

import org.apache.http.client.CookieStore
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.util.EntityUtils

class HttpSessionClient(
    private val httpClient: CloseableHttpClient,
    private val cookieStore: CookieStore
) {

     fun get(url: String): HttpResponse {
        val request = HttpGet(url)
        applyHeaders(request)
        val response = httpClient.execute(request)
        val body = EntityUtils.toString(response?.entity)
        val headers = response?.allHeaders?.associate { it.name to listOf(it.value) } ?: emptyMap()
        return HttpResponse(body, headers)
    }

     fun post(url: String, body: String): HttpResponse {
        val request = HttpPost(url)
        request.setHeader("Content-Type", "application/x-www-form-urlencoded")
        applyHeaders(request)
        request.entity = StringEntity(body)

        val response = httpClient.execute(request)
        val responseBody = EntityUtils.toString(response?.entity)
        val headers = response?.allHeaders?.associate { it.name to listOf(it.value) } ?: emptyMap()
        return HttpResponse(responseBody, headers)
    }

     fun getCookies(): Map<String, String> {
        return cookieStore.cookies.associate { it.name to it.value }
    }

    private fun applyHeaders(request: org.apache.http.client.methods.HttpRequestBase) {
        request.setHeader("User-Agent", "Mozilla/5.0")
        request.setHeader("Accept", "text/html,application/xhtml+xml")
        request.setHeader("Accept-Language", "en-US,en;q=0.9")
    }

    fun close() {
        httpClient.close()
    }

    data class HttpResponse(
        val body: String,
        val headers: Map<String, List<String>>
    )
}