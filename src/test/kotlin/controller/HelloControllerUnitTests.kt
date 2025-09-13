package es.unizar.webeng.hello.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.ui.Model
import org.springframework.ui.ExtendedModelMap

class HelloControllerUnitTests {
    private lateinit var controller: HelloController
    private lateinit var model: Model
    
    @BeforeEach
    fun setup() {
        controller = HelloController("Test Message")
        model = ExtendedModelMap()
    }
    
    @Test
    fun `greet returns expected values for different hours`() {
        assertThat(greet(4)).isEqualTo("Good Night")
        assertThat(greet(8)).isEqualTo("Good Morning")
        assertThat(greet(14)).isEqualTo("Good Afternoon")
        assertThat(greet(22)).isEqualTo("Good Night")
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
        val apiController = HelloApiController()
        val response = apiController.helloApi("Test")
        
        assertThat(response).containsKey("message")
        assertThat(response).containsKey("timestamp")
        assertThat(response["message"]).matches("^(Good Morning|Good Afternoon|Good Night), Test!$")
        assertThat(response["timestamp"]).isNotNull()
    }
}
