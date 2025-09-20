package es.unizar.webeng.hello.controller

import es.unizar.webeng.hello.entity.User
import es.unizar.webeng.hello.enum.Role
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.boot.test.web.server.LocalServerPort

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT,
    properties = ["spring.h2.console.enabled=false"])
@TestPropertySource("classpath:application-test.properties")
class RateLimiterMvcTest() {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `rate limiter triggers 429 for unauthenticated user`() {
        val url = "/api/hello"

        repeat(5) {
            mockMvc.perform(MockMvcRequestBuilders.get(url).with { request ->
                request.remoteAddr = "127.0.0.2"
                request
            }).andExpect(status().isCreated)
        }

        mockMvc.perform(MockMvcRequestBuilders.get(url).with { request ->
            request.remoteAddr = "127.0.0.2"
            request
        }).andExpect(status().isTooManyRequests)
    }

    @Test
    fun `rate limiter triggers 429 for authenticated user`() {
        val url = "/api/hello"
        val testUser = User(username = "testRate", password = "", role = Role.USER)

        repeat(5) {
            mockMvc.perform(MockMvcRequestBuilders.get(url)
                .with(user(testUser.username)) 
            ).andExpect(status().isCreated)
        }

        mockMvc.perform(MockMvcRequestBuilders.get(url)
            .with(user(testUser.username))
        ).andExpect(status().isTooManyRequests)
    }
}