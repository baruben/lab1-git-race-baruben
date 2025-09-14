package es.unizar.webeng.hello.controller

import es.unizar.webeng.hello.data.*
import es.unizar.webeng.hello.service.*
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

@WebMvcTest(HelloController::class, HelloApiController::class)
class HelloControllerMVCTests {
    @Value("\${app.message:Welcome to the Modern Web App!}")
    private lateinit var message: String

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var greetingServiceMock: GreetingService

    @MockBean
    private lateinit var userServiceMock: UserService

    @BeforeEach
    fun setup() {
        whenever(userServiceMock.guest).thenReturn(
            User(username = "", mail = "", password = "", userType = UserType.GUEST)
        )

        whenever(greetingServiceMock.createGreeting(any(), any(), any())).thenAnswer { invocation ->
            val name = invocation.getArgument<String>(0)
            val requestType = invocation.getArgument<RequestType>(1)
            val user = invocation.getArgument<User>(2)

            Greeting(name = name, requestType = requestType, user = user)
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
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message",
                matchesPattern("^(Good Morning|Good Afternoon|Good Night), Test!$")))
            .andExpect(jsonPath("$.timestamp").exists())
    }
}

