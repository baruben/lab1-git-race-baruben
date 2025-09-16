package es.unizar.webeng.hello.controller

import es.unizar.webeng.hello.entity.*
import es.unizar.webeng.hello.enum.*
import es.unizar.webeng.hello.service.GreetingService
import es.unizar.webeng.hello.service.UserService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.ui.Model
import org.springframework.ui.ExtendedModelMap
import org.mockito.kotlin.*

class HelloControllerUnitTests {
    private lateinit var controller: HelloController
    private lateinit var model: Model
    
    private lateinit var greetingServiceMock: GreetingService
    private lateinit var userServiceMock: UserService

    @BeforeEach
    fun setup() {
        greetingServiceMock = mock()
        userServiceMock = mock()

        whenever(userServiceMock.getSessionUser()).thenReturn(
            User(username = "", password = "", role = Role.GUEST)
        )

        whenever(greetingServiceMock.create(any(), any(), any())).thenAnswer { invocation ->
            val name = invocation.getArgument<String>(0)
            val requestType = invocation.getArgument<RequestType>(1)
            val user = invocation.getArgument<User>(2)

            Greeting(name = name, requestType = requestType, user = user)
        }

        controller = HelloController(
            message = "Test Message",
            greetingService = greetingServiceMock,
            userService = userServiceMock
        )
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
        
        assertThat(view).isEqualTo("welcome")
        val message = model.getAttribute("message") as String
        assertThat(message).matches("^(Good Morning|Good Afternoon|Good Night), Developer!$")
        assertThat(model.getAttribute("name")).isEqualTo("Developer")
    }

    @Test
    fun `should return API response with timestamp`() {
        val apiController = HelloApiController(
            greetingService = greetingServiceMock,
            userService = userServiceMock
        )
        val response = apiController.helloApi("Test")
        
        assertThat(response).containsKey("message")
        assertThat(response).containsKey("timestamp")
        assertThat(response["message"]).matches("^(Good Morning|Good Afternoon|Good Night), Test!$")
        assertThat(response["timestamp"]).isNotNull()
    }
}
