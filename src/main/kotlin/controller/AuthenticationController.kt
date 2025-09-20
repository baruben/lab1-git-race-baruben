package es.unizar.webeng.hello.controller

import es.unizar.webeng.hello.service.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.RememberMeServices
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag

@Controller
class AuthenticationController(
    private val userService: UserService,
    private val authenticationManager: AuthenticationManager,
    private val rememberMeServices: PersistentTokenBasedRememberMeServices
) {
    @GetMapping("/login")
    fun loginForm(): String = "login"

    @GetMapping("/signup")
    fun signupForm(): String = "signup"

    @PostMapping("/signup")
    fun signup(
        @RequestParam username: String,
        @RequestParam password: String,
        model: Model,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): String {
        try {
            userService.create(username, password)

            val authRequest = UsernamePasswordAuthenticationToken(username, password)
            val authentication: Authentication = authenticationManager.authenticate(authRequest)
            SecurityContextHolder.getContext().authentication = authentication

            rememberMeServices.loginSuccess(request, response, authentication)

            return "redirect:/"

        } catch (e: IllegalArgumentException) {
            return "redirect:/signup?error"
        }
    }
}

@RestController
@Tag(
    name = "Authentication",
    description = "Endpoints for authentication info."
)
class AuthenticationApiController() {
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