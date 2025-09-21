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


/**
 * Controller for handling user authentication, login, and signup.
 *
 * Provides endpoints for login and signup forms, as well as handling
 * signup submission with immediate authentication and remember-me support.
 *
 * @property userService Service for user creation and management.
 * @property authenticationManager Used to authenticate newly created users.
 * @property rememberMeServices Used to handle remember-me token creation upon signup.
 */
@Controller
class AuthenticationController(
    private val userService: UserService,
    private val authenticationManager: AuthenticationManager,
    private val rememberMeServices: PersistentTokenBasedRememberMeServices
) {

    /**
     * GET endpoint for the login page.
     *
     * @return The login view name.
     */
    @GetMapping("/login")
    fun loginForm(): String = "login"

    /**
     * GET endpoint for the signup page.
     *
     * @return The signup view name.
     */
    @GetMapping("/signup")
    fun signupForm(): String = "signup"

    /**
     * POST endpoint for user signup.
     *
     * Creates a new user, authenticates them immediately, and triggers
     * remember-me login.
     *
     * ---
     *
     * ## Behavior:
     * - Create user via [UserService.create].
     * - Authenticate using [AuthenticationManager].
     * - Set authentication in [SecurityContextHolder].
     * - Trigger [PersistentTokenBasedRememberMeServices.loginSuccess].
     * - Redirect to homepage on success or signup page with error on failure.
     *
     * ---
     *
     * @param username Desired username for the new user.
     * @param password Desired password for the new user.
     * @param request HTTP servlet request.
     * @param response HTTP servlet response.
     * @return Redirect URL: "/" on success, "/signup?error" on failure.
     */
    @PostMapping("/signup")
    fun signup(
        @RequestParam username: String,
        @RequestParam password: String,
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