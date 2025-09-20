package es.unizar.webeng.hello.entity

import es.unizar.webeng.hello.enum.Endpoint
import es.unizar.webeng.hello.enum.TimeOfDay
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime

class GreetingUnitTests {

    private val user = User(username = "test", password = "password")

    @Test
    fun `should return MORNING for 6 AM`() {
        val timestamp = OffsetDateTime.parse("2025-09-20T06:00:00+00:00")
        val greeting = Greeting(endpoint = Endpoint.WEB, user = user, name = "test", timestamp = timestamp)

        assertThat(greeting.timeOfDay).isEqualTo(TimeOfDay.MORNING)
    }

    @Test
    fun `should return AFTERNOON for 15 PM`() {
        val timestamp = OffsetDateTime.parse("2025-09-20T15:00:00+00:00")
        val greeting = Greeting(endpoint = Endpoint.WEB, user = user, name = "test", timestamp = timestamp)

        assertThat(greeting.timeOfDay).isEqualTo(TimeOfDay.AFTERNOON)
    }

    @Test
    fun `should return NIGHT for 23 PM`() {
        val timestamp = OffsetDateTime.parse("2025-09-20T23:00:00+00:00")
        val greeting = Greeting(endpoint = Endpoint.WEB, user = user, name = "test", timestamp = timestamp)

        assertThat(greeting.timeOfDay).isEqualTo(TimeOfDay.NIGHT)
    }
}