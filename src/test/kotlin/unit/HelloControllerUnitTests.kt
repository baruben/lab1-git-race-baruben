package es.unizar.webeng.hello.controller

import es.unizar.webeng.hello.entity.Greeting
import es.unizar.webeng.hello.enum.*
import es.unizar.webeng.hello.entity.User
import es.unizar.webeng.hello.response.GreetingResponse
import es.unizar.webeng.hello.service.GreetingService
import es.unizar.webeng.hello.service.UserService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.ui.ExtendedModelMap
import org.springframework.ui.Model
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.OffsetDateTime

class HelloControllerUnitTests {
    private lateinit var controller: HelloController
    private lateinit var apiController: HelloApiController
    private lateinit var model: Model
    
    private lateinit var greetingServiceMock: GreetingService
    private lateinit var userServiceMock: UserService

    @BeforeEach
    fun setup() {
        initMocks()
        initControllers()
        initModel()
    }

    private fun initMocks() {
        greetingServiceMock = mock()
        userServiceMock = mock()
        
        whenever(userServiceMock.getSessionUser()).thenReturn(
            User(username = "test", password = "password", role = Role.USER)
        )

        whenever(greetingServiceMock.create(any(), any(), any())).thenAnswer { invocation ->
            val name = invocation.getArgument<String>(0)
            val endpoint = invocation.getArgument<Endpoint>(1)
            val user = invocation.getArgument<User>(2)

            Greeting(name = name, endpoint = endpoint, user = user)
        }

        val user = userServiceMock.getSessionUser()
        val greeting1 = Greeting(name = "Alice", endpoint = Endpoint.API, user = user)
        val greeting2 = Greeting(name = "Bob", endpoint = Endpoint.WEB, user = user)
        val greetings = listOf(greeting1, greeting2)

        whenever(greetingServiceMock.findAllByUserOrderByTimestampDesc(user)).thenReturn(greetings)
    }
    
    private fun initControllers() {
        controller = HelloController(
            message = "Test Message",
            greetingService = greetingServiceMock,
            userService = userServiceMock
        )
        apiController = HelloApiController(
            greetingService = greetingServiceMock,
            userService = userServiceMock
        )
    }

    private fun initModel() {
        model = ExtendedModelMap()
    }

    @Test
    fun `should return welcome view with default message`() {
        val view = controller.welcome(model, "")
        
        assertThat(view).isEqualTo("welcome")
        assertThat(model.getAttribute("message")).isEqualTo("Test Message")
        assertThat(model.getAttribute("name")).isEqualTo("")
    }
    
    @Test
    fun `should return welcome view with personalized message`() {
        val view = controller.welcome(model, "Developer")
        val timeOfDay = timestampToTimeOfDay(OffsetDateTime.now())

        assertThat(view).isEqualTo("welcome")
        assertThat(model.getAttribute("message")).isEqualTo("${timeOfDay.message}, Developer!")
        assertThat(model.getAttribute("name")).isEqualTo("Developer")
    }

    @Test
    fun `should return API response with timestamp`() {
        val apiController = HelloApiController(
            greetingService = greetingServiceMock,
            userService = userServiceMock
        )
        val response = apiController.helloApi("Test")
        val timeOfDay = timestampToTimeOfDay(OffsetDateTime.now())
        
        assertThat(response).isInstanceOf(GreetingResponse::class.java)
        assertThat(response.message).isEqualTo("${timeOfDay.message}, Test!")
        assertThat(response.timestamp).isNotNull()
    }
}
