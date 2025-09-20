package es.unizar.webeng.hello.controller

import es.unizar.webeng.hello.enum.Endpoint
import es.unizar.webeng.hello.entity.SecurityUser
import es.unizar.webeng.hello.service.GreetingService
import es.unizar.webeng.hello.service.UserService
import es.unizar.webeng.hello.response.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.Authentication
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.security.SecurityRequirement
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
                endpoint = Endpoint.WEB,
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
@Tag(
    name = "Greeting",
    description = "Endpoints for greetings."
)
class HelloApiController(
    private val greetingService: GreetingService,
    private val userService: UserService
) {
    @Operation(
        summary = "Get a greeting message",
        description = "Returns a greeting message and timestamp. If no name is provided, defaults to 'World'.",
        responses = [
            ApiResponse(responseCode = "201", description = "Greeting successfully recorded")
        ]
    )
    @GetMapping("/api/hello", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    fun helloApi(
        @Parameter(
            description = "Name of the person to greet (defaults to 'World')",
            example = "Alice"
        )
        @RequestParam(defaultValue = "World") name: String): GreetingResponse {
        val user = userService.getSessionUser()

        val greeting = greetingService.create(
            name = name,
            endpoint = Endpoint.API,
            user = user
        )

        return GreetingResponse(
            message = "${greeting.timeOfDay.message}, $name!",
            timestamp = greeting.timestamp.toString()
        )
    }

    @Operation(
        summary = "Get greeting history",
        description = "Returns a list of all the greetings recorded for the current user, sorted by timestamp (descending).",
        security = [SecurityRequirement(name = "cookieAuth")],
        responses = [
            ApiResponse(responseCode = "200", description = "List of greeting history"),
            ApiResponse(responseCode = "401", description = "Unauthorized - authentication required", 
                content = [Content(schema = Schema(hidden = true))])
        ]
    )
    @GetMapping("/api/history", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun historyApi(): List<GreetingHistoryResponse> {
        val user = userService.getSessionUser()
        val greetings = greetingService.findAllByUserOrderByTimestampDesc(user)

        return greetings.map { greeting ->
            GreetingHistoryResponse(
                message = "${greeting.timeOfDay.message}, ${greeting.name}!",
                endpoint = greeting.endpoint.name,
                timestamp = greeting.timestamp.toString()
            )
        }
    }
}

@RestController
@Tag(
    name = "User",
    description = "Endpoints for users."
)
class UserApiController() {
    @Operation(
        summary = "Check current authentication info",
        description = "Returns the authentication type, username, and authorities if the user is authenticated.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Authentication details string"
            )
        ]
    )
    @GetMapping("/api/whoami")
    fun whoamiApi(auth: Authentication?): String {
        return if (auth == null) {
            "Not authenticated"
        } else {
            "Auth type: ${auth.javaClass.simpleName}, name: ${auth.name}, authorities: ${auth.authorities}"
        }
    }

}