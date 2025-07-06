package kedokato.myhoubackend.controller.v1

import kedokato.myhoubackend.domain.respone.HealthResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("v1/api")
class HealthController {


    @GetMapping("/health")
    fun healthCheck() = HealthResponse("OK", "Server online", System.currentTimeMillis())


}