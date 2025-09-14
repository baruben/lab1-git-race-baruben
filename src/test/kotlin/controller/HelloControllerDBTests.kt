package es.unizar.webeng.hello.controller

import es.unizar.webeng.hello.data.Greeting
import es.unizar.webeng.hello.data.RequestType
import es.unizar.webeng.hello.data.UserType
import es.unizar.webeng.hello.repository.GreetingRepository
import es.unizar.webeng.hello.service.UserService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import java.time.OffsetDateTime

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class HelloControllerDBTests {
    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var greetingRepository: GreetingRepository
    
    @Autowired
    private lateinit var userService: UserService
    
    @BeforeEach
    fun setup() {
        greetingRepository.deleteAll()
    }
    
    @Test
    fun `should insert greeting in DB when accessing home page`() {
        val response = restTemplate.getForEntity("http://localhost:$port?name=Test", String::class.java)
        
        val greetings = greetingRepository.findAll()
        assertThat(greetings).hasSize(1)
        val savedGreeting = greetings.first()

        assertThat(savedGreeting.requestType).isEqualTo(RequestType.WEB)
        assertThat(savedGreeting.name).isEqualTo("Test")
        assertThat(savedGreeting.user.userType).isEqualTo(UserType.GUEST)
        assertThat(savedGreeting.timestamp).isInstanceOf(OffsetDateTime::class.java)
    }

    @Test
    fun `should insert greeting in DB when accessing API`() {
        val response = restTemplate.getForEntity("http://localhost:$port/api/hello?name=Test", String::class.java)
        
        val greetings = greetingRepository.findAll()
        assertThat(greetings).hasSize(1)
        val savedGreeting = greetings.first()

        assertThat(savedGreeting.requestType).isEqualTo(RequestType.API)
        assertThat(savedGreeting.name).isEqualTo("Test")
        assertThat(savedGreeting.user.userType).isEqualTo(UserType.GUEST)
        assertThat(savedGreeting.timestamp).isInstanceOf(OffsetDateTime::class.java)
    }
}
