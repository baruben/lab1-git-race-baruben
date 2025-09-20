package es.unizar.webeng.hello.controller

import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag

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