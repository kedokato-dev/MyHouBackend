package kedokato.myhoubackend.controller.v1

import kedokato.myhoubackend.domain.request.LoginRequest
import kedokato.myhoubackend.domain.respone.LoginResponse
import kedokato.myhoubackend.service.CasLoginService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("v1/api")
class LoginController(
    private val casLoginService: CasLoginService
) {

    private val logger = LoggerFactory.getLogger(LoginController::class.java)


    @PostMapping("/login", consumes = ["application/json"])
    suspend fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<LoginResponse> {
        val loginResult = casLoginService.login(loginRequest.username, loginRequest.password)
        return if (loginResult.success) {
            logger.info("User logged in successfully ${loginRequest.username}")
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

}