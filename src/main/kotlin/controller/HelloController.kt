package es.unizar.webeng.hello.controller

import es.unizar.webeng.hello.enum.RequestType
import es.unizar.webeng.hello.entity.SecurityUser
import es.unizar.webeng.hello.service.GreetingService
import es.unizar.webeng.hello.service.UserService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.Authentication
import java.time.OffsetDateTime

@Controller
class HelloController(
    @param:Value("\${app.message:Hello World}") 
    private val message: String,
    private val greetingService: GreetingService,
    private val userService: UserService
) {

    @GetMapping("/")
    fun welcome(
        model: Model,
        @RequestParam(defaultValue = "") name: String
    ): String {
        val user = userService.getSessionUser()

        val greetingMessage = if (name.isNotBlank()) {
            val greeting = greetingService.create(
                name = name,
                requestType = RequestType.WEB,
                user = user
            )
            "${greeting.timeOfDay.message}, $name!"
        } else {
            message
        }
        model.addAttribute("message", greetingMessage)
        model.addAttribute("name", name)
        return "welcome"
    }
}

@RestController
class HelloApiController(
    private val greetingService: GreetingService,
    private val userService: UserService
) {
    
    @GetMapping("/api/hello", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun helloApi(@RequestParam(defaultValue = "World") name: String): Map<String, String> {
        val user = userService.getSessionUser()

        val greeting = greetingService.create(
            name = name,
            requestType = RequestType.API,
            user = user
        )

        return mapOf(
            "message" to "${greeting.timeOfDay.message}, $name!",
            "timestamp" to greeting.timestamp.toString()
        )
    }

    @GetMapping("/api/whoami")
    fun whoamiApi(auth: Authentication?): String {
        return if (auth == null) {
            "Not authenticated"
        } else {
            "Auth type: ${auth.javaClass.simpleName}, name: ${auth.name}, authorities: ${auth.authorities}"
        }
    }

    @GetMapping("/api/history", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun historyApi(): List<Map<String, String>> {
        val user = userService.getSessionUser()
        val greetings = greetingService.findAllByUserOrderByTimestampDesc(user)

        return greetings.map { greeting ->
            mapOf(
                "message" to "${greeting.timeOfDay.message}, ${greeting.name}!",
                "from" to greeting.requestType.name,
                "timestamp" to greeting.timestamp.toString()
            )
        }
    }
}
