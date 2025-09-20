package es.unizar.webeng.hello.repository

import es.unizar.webeng.hello.entity.Greeting
import es.unizar.webeng.hello.entity.User
import es.unizar.webeng.hello.enum.Endpoint
import es.unizar.webeng.hello.enum.Role
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import java.time.OffsetDateTime

@DataJpaTest
class GreetingRepositoryTests  {

    @Autowired
    private lateinit var greetingRepository: GreetingRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    lateinit var guest: User
    lateinit var user: User
    private val limit = 3

    @BeforeEach
    fun setup() {
        guest = userRepository.save(User(username = "", password = "", role = Role.GUEST))
        user = userRepository.save(User(username = "test", password = "password", role = Role.USER))
    }

    @Test
    fun `should count greetings, group by endpoint and order by count descending`() {
        greetingRepository.save(Greeting(user = user, name = "Alice", endpoint = Endpoint.WEB))
        greetingRepository.save(Greeting(user = guest, name = "Bob", endpoint = Endpoint.WEB))
        greetingRepository.save(Greeting(user = user, name = "Charlie", endpoint = Endpoint.API))

        val results = greetingRepository.countPerEndpoint()

        assertThat(results).containsExactly(
            Pair(Endpoint.WEB, 2L),
            Pair(Endpoint.API, 1L)
        )
    }

    @Test
    fun `should count greetings, group by user_role and order by count descending`() {
        greetingRepository.save(Greeting(user = user, name = "Alice", endpoint = Endpoint.WEB))
        greetingRepository.save(Greeting(user = user, name = "Bob", endpoint = Endpoint.API))
        greetingRepository.save(Greeting(user = guest, name = "Charlie", endpoint = Endpoint.API))

        val results = greetingRepository.countPerUserRole()
        assertThat(results).containsExactly(
            Pair(Role.USER, 2L),
            Pair(Role.GUEST, 1L)
        )
    }

    @Test
    fun `should count greetings, group by timeOfDay and order by count descending`() {
        val morning = OffsetDateTime.now().withHour(6)
        val afternoon = OffsetDateTime.now().withHour(13)
        val night = OffsetDateTime.now().withHour(22)

        greetingRepository.save(Greeting(user = guest, name = "Alice", endpoint = Endpoint.WEB, timestamp = morning))
        greetingRepository.save(Greeting(user = guest, name = "Bob", endpoint = Endpoint.API, timestamp = afternoon))
        greetingRepository.save(Greeting(user = user, name = "Charlie", endpoint = Endpoint.WEB, timestamp = night))
        greetingRepository.save(Greeting(user = user, name = "Dave", endpoint = Endpoint.WEB, timestamp = morning))

        val results = greetingRepository.countPerTimeOfDay()
        assertThat(results).containsExactly(
            Pair("MORNING", 2L),
            Pair("AFTERNOON", 1L),
            Pair("NIGHT", 1L)
        )
    }

    @Test
    fun `should count greetings, group by timestamp_date and order by count descending`() {
        val date1 = OffsetDateTime.now().withHour(10)
        val date2 = OffsetDateTime.now().minusDays(1).withHour(15)

        greetingRepository.save(Greeting(user = user, name = "Alice", endpoint = Endpoint.WEB, timestamp = date1))
        greetingRepository.save(Greeting(user = user, name = "Bob", endpoint = Endpoint.API, timestamp = date1))
        greetingRepository.save(Greeting(user = guest, name = "Charlie", endpoint = Endpoint.WEB, timestamp = date2))

        val results = greetingRepository.countPerDate(PageRequest.of(0, limit))
        assertThat(results).containsExactly(
            Pair(date1.toLocalDate().toString(), 2L),
            Pair(date2.toLocalDate().toString(), 1L)
        )
    }

    @Test
    fun `should count greetings, group by timestamp_hour and order by count descending`() {
        val hour1 = OffsetDateTime.now().withHour(9)
        val hour2 = OffsetDateTime.now().withHour(15)

        greetingRepository.save(Greeting(user = user, name = "Alice", endpoint = Endpoint.WEB, timestamp = hour1))
        greetingRepository.save(Greeting(user = user, name = "Bob", endpoint = Endpoint.API, timestamp = hour2))
        greetingRepository.save(Greeting(user = guest, name = "Charlie", endpoint = Endpoint.WEB, timestamp = hour1))

        val results = greetingRepository.countPerHour(PageRequest.of(0, limit))
        assertThat(results).containsExactlyInAnyOrder(
            Pair(9, 2L),
            Pair(15, 1L)
        )
    }

    @Test
    fun `should count greetings, group by name and order by count descending`() {
        greetingRepository.save(Greeting(user = user, name = "alice", endpoint = Endpoint.WEB))
        greetingRepository.save(Greeting(user = user, name = "Alice", endpoint = Endpoint.API))
        greetingRepository.save(Greeting(user = guest, name = "bob", endpoint = Endpoint.WEB))

        val results = greetingRepository.countPerName(PageRequest.of(0, limit))
        assertThat(results).containsExactly(
            Pair("ALICE", 2L),
            Pair("BOB", 1L)
        )
    }

    @Test
    fun `should count greetings' distinct names`() {
        greetingRepository.save(Greeting(user = user, name = "alice", endpoint = Endpoint.WEB))
        greetingRepository.save(Greeting(user = user, name = "Alice", endpoint = Endpoint.API))
        greetingRepository.save(Greeting(user = guest, name = "bob", endpoint = Endpoint.WEB))

        val result = greetingRepository.countDistinctNames()
        assertThat(result).isEqualTo(2L)
    }

    @Test
    fun `should count user's greetings`() {
        greetingRepository.save(Greeting(user = user, name = "Alice", endpoint = Endpoint.WEB))
        greetingRepository.save(Greeting(user = user, name = "Bob", endpoint = Endpoint.API))
        greetingRepository.save(Greeting(user = guest, name = "Charlie", endpoint = Endpoint.WEB))

        val result1 = greetingRepository.countByUser(user)
        val result2 = greetingRepository.countByUser(guest)

        assertThat(result1).isEqualTo(2L)
        assertThat(result2).isEqualTo(1L)
    }

    @Test
    fun `should count user's greetings, group by endpoint and order by count descending`() {
        greetingRepository.save(Greeting(user = user, name = "Alice", endpoint = Endpoint.WEB))
        greetingRepository.save(Greeting(user = user, name = "Bob", endpoint = Endpoint.WEB))
        greetingRepository.save(Greeting(user = user, name = "Charlie", endpoint = Endpoint.API))
        greetingRepository.save(Greeting(user = guest, name = "Dave", endpoint = Endpoint.API))

        val results = greetingRepository.countPerEndpointByUser(user)

        assertThat(results).containsExactly(
            Pair(Endpoint.WEB, 2L),
            Pair(Endpoint.API, 1L)
        )
    }

    @Test
    fun `should count user's greetings, group by timeOfDay and order by count descending`() {
        val morning = OffsetDateTime.now().withHour(6)
        val afternoon = OffsetDateTime.now().withHour(13)
        val night = OffsetDateTime.now().withHour(22)

        greetingRepository.save(Greeting(user = user, name = "Alice", endpoint = Endpoint.WEB, timestamp = morning))
        greetingRepository.save(Greeting(user = user, name = "Bob", endpoint = Endpoint.API, timestamp = afternoon))
        greetingRepository.save(Greeting(user = user, name = "Charlie", endpoint = Endpoint.WEB, timestamp = night))
        greetingRepository.save(Greeting(user = user, name = "Nick", endpoint = Endpoint.API, timestamp = morning))
        greetingRepository.save(Greeting(user = guest, name = "Dave", endpoint = Endpoint.WEB, timestamp = afternoon))

        val results = greetingRepository.countPerTimeOfDayByUser(user)

        assertThat(results).containsExactly(
            Pair("MORNING", 2L),
            Pair("AFTERNOON", 1L),
            Pair("NIGHT", 1L)
        )
    }

    @Test
    fun `should count user's greetings, group by timestamp_date and order by count descending`() {
        val date1 = OffsetDateTime.now().withHour(10)
        val date2 = OffsetDateTime.now().minusDays(1).withHour(15)

        greetingRepository.save(Greeting(user = user, name = "Alice", endpoint = Endpoint.WEB, timestamp = date1))
        greetingRepository.save(Greeting(user = user, name = "Bob", endpoint = Endpoint.API, timestamp = date1))
        greetingRepository.save(Greeting(user = user, name = "Charlie", endpoint = Endpoint.WEB, timestamp = date2))
        greetingRepository.save(Greeting(user = guest, name = "Dave", endpoint = Endpoint.WEB, timestamp = date1))

        val results = greetingRepository.countPerDateByUser(user, PageRequest.of(0, limit))

        assertThat(results).containsExactly(
            Pair(date1.toLocalDate().toString(), 2L),
            Pair(date2.toLocalDate().toString(), 1L)
        )
    }

    @Test
    fun `should count user's greetings, group by timestamp_hour and order by count descending`() {
        val hour1 = OffsetDateTime.now().withHour(9)
        val hour2 = OffsetDateTime.now().withHour(15)

        greetingRepository.save(Greeting(user = user, name = "Alice", endpoint = Endpoint.WEB, timestamp = hour1))
        greetingRepository.save(Greeting(user = user, name = "Bob", endpoint = Endpoint.API, timestamp = hour2))
        greetingRepository.save(Greeting(user = guest, name = "Charlie", endpoint = Endpoint.WEB, timestamp = hour1))

        val results = greetingRepository.countPerHourByUser(user, PageRequest.of(0, limit))

        assertThat(results).containsExactlyInAnyOrder(
            Pair(9, 1L),
            Pair(15, 1L)
        )
    }

    @Test
    fun `should count user's greetings, group by name and order by count descending`() {
        greetingRepository.save(Greeting(user = user, name = "alice", endpoint = Endpoint.WEB))
        greetingRepository.save(Greeting(user = user, name = "Alice", endpoint = Endpoint.API))
        greetingRepository.save(Greeting(user = guest, name = "bob", endpoint = Endpoint.WEB))

        val results = greetingRepository.countPerNameByUser(user, PageRequest.of(0, limit))

        assertThat(results).containsExactly(
            Pair("ALICE", 2L)
        )
    }

    @Test
    fun `should count user's greetings' distinct names`() {
        greetingRepository.save(Greeting(user = user, name = "alice", endpoint = Endpoint.WEB))
        greetingRepository.save(Greeting(user = user, name = "Alice", endpoint = Endpoint.API))
        greetingRepository.save(Greeting(user = guest, name = "bob", endpoint = Endpoint.WEB))

        val result = greetingRepository.countDistinctNamesByUser(user)
        assertThat(result).isEqualTo(1L)
    }
}
