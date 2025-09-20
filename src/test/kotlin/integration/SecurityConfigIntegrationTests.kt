package es.unizar.webeng.hello.config

import es.unizar.webeng.hello.entity.User
import es.unizar.webeng.hello.enum.Role
import es.unizar.webeng.hello.repository.UserRepository
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.mock.web.MockHttpSession
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.TestPropertySource
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT,
    properties = ["spring.h2.console.enabled=false"])
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.properties")
class SecurityConfigIntegrationTests {

    @LocalServerPort
    private var port: Int = 0

    @Autowired 
    private lateinit var mockMvc: MockMvc

    @Autowired 
    private lateinit var userRepository: UserRepository

    @Autowired 
    private lateinit var passwordEncoder: PasswordEncoder

    @BeforeEach
    fun setup() {
        val user = userRepository.findByUsername("testAuth") ?: userRepository.save(
            User(username = "testAuth", password = passwordEncoder.encode("password"), role = Role.USER)
        )
    }

    @Test
    fun `public endpoint should be accessible`() {
        mockMvc.perform(get("/api/whoami"))
            .andExpect(status().isOk)
    }

    @Test
    fun `protected endpoint should require authentication`() {
        mockMvc.perform(get("/api/history"))
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrlPattern("**/login"))
    }

    @Test
    fun `should succeed with valid login credentials and set remember-me cookie`() {
        mockMvc.perform(
            post("/login")
                .param("username", "testAuth")
                .param("password", "password")
                .with(csrf())
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/"))
            .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("remember-me")))
    }

    @Test
    fun `should fail with invalid login credentials`() {
        mockMvc.perform(
            post("/login")
                .param("username", "bob")
                .param("password", "wrong")
                .with(csrf())
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/login?error"))
    }

    @Test
    fun `signup should set remember-me cookie`() {
        mockMvc.perform(
            post("/signup")
                .param("username", "bob")
                .param("password", "secret")
                .with(csrf())
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("remember-me")))
    }

    @Test
    fun `logout should clear remember-me cookie`() {
        // Perform login
        val loginResult = mockMvc.perform(
            post("/login")
                .param("username", "testAuth")
                .param("password", "password")
                .with(csrf())
        ).andExpect(status().is3xxRedirection)
        .andReturn()

        val session = loginResult.request.session as MockHttpSession

        // Perform logout using the same session
        mockMvc.perform(
            post("/logout")
                .session(session)
                .with(csrf())
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(cookie().maxAge("remember-me", 0))
    }
}
