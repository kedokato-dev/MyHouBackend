package kedokato.myhoubackend.controller

import kedokato.myhoubackend.domain.respone.HealthResponse
import kedokato.myhoubackend.domain.respone.LoginResponse
import kedokato.myhoubackend.service.CasLoginService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class CrawlerController(
    private val casLoginService: CasLoginService
) {

    @PostMapping("/login")
    suspend fun login(@RequestParam username: String, @RequestParam password: String): ResponseEntity<LoginResponse> {
        val loginResult = casLoginService.login(username, password)
        return if (loginResult.success) {
            ResponseEntity.ok(
                LoginResponse(
                    success = true,
                    message = loginResult.message,
                    authCookie = loginResult.authCookie,
                    sessionId = loginResult.sessionId
                )
            )
        } else {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                LoginResponse(
                    success = false,
                    message = loginResult.message,
                    authCookie = null,
                    sessionId = null
                )
            )
        }
    }

    @GetMapping("/healthy")
    fun healthCheck(): ResponseEntity<HealthResponse> {
        return ResponseEntity.ok(HealthResponse(status = "healthy", message = "Service is running"))
    }



}