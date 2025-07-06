package kedokato.myhoubackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class MyHouBackendApplication

fun main(args: Array<String>) {
    runApplication<MyHouBackendApplication>(*args)
}
