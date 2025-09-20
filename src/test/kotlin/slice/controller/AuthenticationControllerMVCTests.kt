package es.unizar.webeng.hello.controller

import es.unizar.webeng.hello.filter.RateLimitFilter
import es.unizar.webeng.hello.entity.User
import es.unizar.webeng.hello.entity.SecurityUser
import es.unizar.webeng.hello.service.UserService
import es.unizar.webeng.hello.repository.TokenRepository
import es.unizar.webeng.hello.configuration.SecurityConfig
import es.unizar.webeng.hello.service.SecurityUserService
import es.unizar.webeng.hello.enum.Role
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.view
import org.hamcrest.Matchers.containsString
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.context.annotation.Import
import org.springframework.beans.factory.annotation.Value
import org.springframework.test.context.TestPropertySource

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf


@WebMvcTest(AuthenticationController::class, AuthenticationApiController::class)
@AutoConfigureMockMvc(addFilters = false)
class AuthenticationControllerMVCTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var userService: UserService

    @MockBean 
    private lateinit var authenticationManager: AuthenticationManager

    @MockBean 
    private lateinit var rememberMeServices: PersistentTokenBasedRememberMeServices

    @MockBean
    private lateinit var rateLimitFilter: RateLimitFilter

    @BeforeEach
    fun setup() {
        whenever(userService.getSessionUser()).thenReturn(
            User(username = "", password = "", role = Role.GUEST)
        )

        whenever(userService.create(any(), any())).thenAnswer { invocation ->
            val username = invocation.getArgument<String>(0)
            val password = invocation.getArgument<String>(1)
            User(username = username, password = password, role = Role.USER)
        }
    }

    @Test
    fun `should return login page`() {
        mockMvc.perform(get("/login"))
            .andExpect(status().isOk)
            .andExpect(view().name("login"))
    }

    @Test
    fun `should return signup page`() {
        mockMvc.perform(get("/signup"))
            .andExpect(status().isOk)
            .andExpect(view().name("signup"))
    }

    @Test
    fun `should create user and authenticate`() {
        val auth: Authentication = UsernamePasswordAuthenticationToken("test", "password")
        whenever(authenticationManager.authenticate(any())).thenReturn(auth)

        mockMvc.perform(
            post("/signup")
                .param("username", "test")
                .param("password", "password")
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(header().string("Location", "/"))

        verify(userService).create("test", "password")
        verify(authenticationManager).authenticate(any())
        verify(rememberMeServices).loginSuccess(any(), any(), any())
        assert(SecurityContextHolder.getContext().authentication.name == "test")
    }

    @Test
    fun `should return 'Not authenticated'`() {
        mockMvc.perform(get("/api/whoami"))
            .andExpect(status().isOk)
            .andExpect(content().string("Not authenticated"))
    }
}
