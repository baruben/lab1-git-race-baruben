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
    
    private lateinit var greetingService: GreetingService
    private lateinit var userService: UserService

    @BeforeEach
    fun setup() {
        model = ExtendedModelMap()

        greetingService = mock()
        userService = mock()
        
        whenever(userService.getSessionUser()).thenReturn(
            User(username = "test", password = "password", role = Role.USER)
        )

        whenever(greetingService.create(any(), any(), any())).thenAnswer { invocation ->
            val name = invocation.getArgument<String>(0)
            val endpoint = invocation.getArgument<Endpoint>(1)
            val user = invocation.getArgument<User>(2)

            Greeting(name = name, endpoint = endpoint, user = user)
        }

        controller = HelloController(
            message = "Test Message",
            greetingService = greetingService,
            userService = userService
        )

        apiController = HelloApiController(
            greetingService = greetingService,
            userService = userService
        )
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
    fun `should return hello API response with timestamp`() {
        val response = apiController.helloApi("Test")
        val timeOfDay = timestampToTimeOfDay(OffsetDateTime.now())
        
        assertThat(response).isInstanceOf(GreetingResponse::class.java)
        assertThat(response.message).isEqualTo("${timeOfDay.message}, Test!")
        assertThat(response.timestamp).isNotNull()
    }

    @Test
    fun `should return history API response with greeting history`() {
        val user = userService.getSessionUser()
        val greeting1 = Greeting(name = "Alice", endpoint = Endpoint.API, user = user)
        val greeting2 = Greeting(name = "Bob", endpoint = Endpoint.WEB, user = user)
        val greetings = listOf(greeting1, greeting2)

        whenever(greetingService.findAllByUserOrderByTimestampDesc(user)).thenReturn(greetings)

        val response = apiController.historyApi()

        assertThat(response).hasSize(2)
        assertThat(response[0].message).isEqualTo("${greeting1.timeOfDay.message}, ${greeting1.name}!")
        assertThat(response[0].endpoint).isEqualTo(greeting1.endpoint.name)
        assertThat(response[0].timestamp).isEqualTo(greeting1.timestamp.toString())

        assertThat(response[1].message).isEqualTo("${greeting2.timeOfDay.message}, ${greeting2.name}!")
        assertThat(response[1].endpoint).isEqualTo(greeting2.endpoint.name)
        assertThat(response[1].timestamp).isEqualTo(greeting2.timestamp.toString())
    }
}
