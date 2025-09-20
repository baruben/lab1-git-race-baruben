package es.unizar.webeng.hello.controller

import es.unizar.webeng.hello.entity.Greeting
import es.unizar.webeng.hello.entity.User
import es.unizar.webeng.hello.enum.*
import es.unizar.webeng.hello.filter.RateLimitFilter
import es.unizar.webeng.hello.service.GreetingService
import es.unizar.webeng.hello.service.UserService
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.OffsetDateTime


@WebMvcTest(HelloController::class, HelloApiController::class)
@AutoConfigureMockMvc(addFilters = false)
class HelloControllerMVCTests {
    @Value("\${app.message:Welcome to the Modern Web App!}")
    private lateinit var message: String

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var greetingServiceMock: GreetingService

    @MockBean
    private lateinit var userServiceMock: UserService

    @MockBean
    private lateinit var rateLimitFilter: RateLimitFilter

    @BeforeEach
    fun setup() {
        whenever(userServiceMock.getSessionUser()).thenReturn(
            User(username = "", password = "", role = Role.GUEST)
        )

        whenever(greetingServiceMock.create(any(), any(), any())).thenAnswer { invocation ->
            val name = invocation.getArgument<String>(0)
            val endpoint = invocation.getArgument<Endpoint>(1)
            val user = invocation.getArgument<User>(2)

            Greeting(name = name, endpoint = endpoint, user = user)
        }
    }

    @Test
    fun `should return home page with default message`() {
        mockMvc.perform(get("/"))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(view().name("welcome"))
            .andExpect(model().attribute("message", equalTo(message)))
            .andExpect(model().attribute("name", equalTo("")))
    }
    
    @Test
    fun `should return home page with personalized message`() {
        val timeOfDay = timestampToTimeOfDay(OffsetDateTime.now())

        mockMvc.perform(get("/").param("name", "Developer"))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(view().name("welcome"))
            .andExpect(model().attribute("message", equalTo("${timeOfDay.message}, Developer!")))
            .andExpect(model().attribute("name", equalTo("Developer")))
    }
    
    @Test
    fun `should return API response as JSON`() {
        val timeOfDay = timestampToTimeOfDay(OffsetDateTime.now())

        mockMvc.perform(get("/api/hello").param("name", "Test"))
            .andDo(print())
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message", equalTo("${timeOfDay.message}, Test!")))
            .andExpect(jsonPath("$.timestamp").exists())
    }
}

@SpringBootTest
@AutoConfigureMockMvc
class RateLimiterMvcTest() {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `rate limiter triggers 429 for unauthenticated user`() {
        val url = "/api/hello"

        repeat(50) {
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
        val testUser = User(username = "testuser", password = "", role = Role.USER)

        repeat(50) {
            mockMvc.perform(MockMvcRequestBuilders.get(url)
                .with(user(testUser.username)) 
            ).andExpect(status().isCreated)
        }

        mockMvc.perform(MockMvcRequestBuilders.get(url)
            .with(user(testUser.username))
        ).andExpect(status().isTooManyRequests)
    }
}