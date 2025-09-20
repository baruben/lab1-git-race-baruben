package es.unizar.webeng.hello.controller

import es.unizar.webeng.hello.service.StatisticsService
import es.unizar.webeng.hello.service.UserService
import es.unizar.webeng.hello.response.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(
    name = "Statistics",
    description = "Endpoints for statistics."
)
class StatisticsApiController(
    private val statisticsService: StatisticsService,
    private val userService: UserService
) {
    @Operation(
        summary = "Get global statistics",
        description = "Returns different global statistics (user usage, popular names...) from the web.",
        responses = [
            ApiResponse(responseCode = "200", description = "List of global statistics")
        ]
    )
    @GetMapping("/api/statistics", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    fun statisticsApi(): StatisticsResponse {
        return statisticsService.getStatistics()
    }

    @Operation(
        summary = "Get the current user's statistics",
        description = "Returns different statistics (user usage, popular names...) from the web's current user.",
        security = [SecurityRequirement(name = "cookieAuth")],
        responses = [
            ApiResponse(responseCode = "200", description = "List of the current user's statistics"),
            ApiResponse(responseCode = "401", description = "Unauthorized - authentication required", 
                content = [Content(schema = Schema(hidden = true))])
        ]
    )
    @GetMapping("/api/myStatistics", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun myStatisticsApi(): MyStatisticsResponse {
        val user = userService.getSessionUser()

        return statisticsService.getMyStatistics(user)
    }
}