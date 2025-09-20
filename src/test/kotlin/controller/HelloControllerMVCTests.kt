package es.unizar.webeng.hello.controller

import es.unizar.webeng.hello.entity.*
import es.unizar.webeng.hello.filter.*
import es.unizar.webeng.hello.enum.*
import es.unizar.webeng.hello.service.*
import es.unizar.webeng.hello.response.*
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matchers.matchesPattern
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.boot.test.mock.mockito.MockBean
import org.mockito.kotlin.*
import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.request.RequestPostProcessor
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user


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
        mockMvc.perform(get("/").param("name", "Developer"))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(view().name("welcome"))
            .andExpect(model().attribute("message", 
                matchesPattern("^(Good Morning|Good Afternoon|Good Night), Developer!$")))
            .andExpect(model().attribute("name", equalTo("Developer")))
    }
    
    @Test
    fun `should return API response as JSON`() {
        mockMvc.perform(get("/api/hello").param("name", "Test"))
            .andDo(print())
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message",
                matchesPattern("^(Good Morning|Good Afternoon|Good Night), Test!$")))
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
            }).andExpect(MockMvcResultMatchers.status().isCreated)
        }

        mockMvc.perform(MockMvcRequestBuilders.get(url).with { request ->
            request.remoteAddr = "127.0.0.2"
            request
        }).andExpect(MockMvcResultMatchers.status().isTooManyRequests)
    }

    @Test
    fun `rate limiter triggers 429 for authenticated user`() {
        val url = "/api/hello"
        val testUser = User(username = "testuser", password = "", role = Role.USER)

        repeat(50) {
            mockMvc.perform(MockMvcRequestBuilders.get(url)
                .with(user(testUser.username)) 
            ).andExpect(MockMvcResultMatchers.status().isCreated)
        }

        mockMvc.perform(MockMvcRequestBuilders.get(url)
            .with(user(testUser.username))
        ).andExpect(MockMvcResultMatchers.status().isTooManyRequests)
    }
}