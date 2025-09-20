package es.unizar.webeng.hello.service

import es.unizar.webeng.hello.entity.User
import es.unizar.webeng.hello.enum.Endpoint
import es.unizar.webeng.hello.enum.Role
import es.unizar.webeng.hello.repository.GreetingRepository
import es.unizar.webeng.hello.response.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageRequest

class StatisticsServiceUnitTests {

    private lateinit var greetingRepository: GreetingRepository

    private lateinit var statisticsService: StatisticsService

    private val limit = 3

    @BeforeEach
    fun setup() {
        greetingRepository = mock()
        statisticsService = StatisticsService(greetingRepository, limit)
    }

    @Test
    fun `should map to StatisticsResponse`() {
        whenever(greetingRepository.count()).thenReturn(10L)
        whenever(greetingRepository.countPerEndpoint()).thenReturn(listOf(Pair(Endpoint.WEB, 4L)))
        whenever(greetingRepository.countPerUserRole()).thenReturn(listOf(Pair(Role.USER, 6L)))
        whenever(greetingRepository.countPerTimeOfDay()).thenReturn(listOf(Pair("MORNING", 5L)))
        whenever(greetingRepository.countPerDate(PageRequest.of(0, limit))).thenReturn(listOf(Pair("2025-09-20", 3L)))
        whenever(greetingRepository.countPerHour(PageRequest.of(0, limit))).thenReturn(listOf(Pair(10, 2L)))
        whenever(greetingRepository.countDistinctNames()).thenReturn(7L)
        whenever(greetingRepository.countPerName(PageRequest.of(0, limit))).thenReturn(listOf(Pair("ALICE", 3L)))

        val response = statisticsService.getStatistics()

        assertThat(response.totalGreetings).isEqualTo(10L)
        assertThat(response.rankingEndpoints).containsExactly(RankingEndpoint("WEB", 4L))
        assertThat(response.rankingRoles).containsExactly(RankingRole("USER", 6L))
        assertThat(response.rankingTimesOfDay).containsExactly(RankingTimeOfDay("MORNING", 5L))
        assertThat(response.topDates).containsExactly(TopDates("2025-09-20", 3L))
        assertThat(response.topHours).containsExactly(TopHours(10, 2L))
        assertThat(response.totalNames).isEqualTo(7L)
        assertThat(response.topNames).containsExactly(TopNames("ALICE", 3L))
    }

    @Test
    fun `should map to MyStatisticsResponse`() {
        val user = User(username = "test", password = "password", role = Role.USER)
        
        whenever(greetingRepository.countByUser(user)).thenReturn(5L)
        whenever(greetingRepository.countPerEndpointByUser(user)).thenReturn(listOf(Pair(Endpoint.API, 3L)))
        whenever(greetingRepository.countPerTimeOfDayByUser(user)).thenReturn(listOf(Pair("AFTERNOON", 2L)))
        whenever(greetingRepository.countPerDateByUser(user, PageRequest.of(0, limit))).thenReturn(listOf(Pair("2025-09-20", 2L)))
        whenever(greetingRepository.countPerHourByUser(user, PageRequest.of(0, limit))).thenReturn(listOf(Pair(14, 1L)))
        whenever(greetingRepository.countPerNameByUser(user, PageRequest.of(0, limit))).thenReturn(listOf(Pair("BOB", 2L)))
        whenever(greetingRepository.countDistinctNamesByUser(user)).thenReturn(2L)

        val response = statisticsService.getMyStatistics(user)

        assertThat(response.totalGreetings).isEqualTo(5L)
        assertThat(response.rankingEndpoints).containsExactly(RankingEndpoint("API", 3L))
        assertThat(response.rankingTimesOfDay).containsExactly(RankingTimeOfDay("AFTERNOON", 2L))
        assertThat(response.topDates).containsExactly(TopDates("2025-09-20", 2L))
        assertThat(response.topHours).containsExactly(TopHours(14, 1L))
        assertThat(response.totalNames).isEqualTo(2L)
        assertThat(response.topNames).containsExactly(TopNames("BOB", 2L))
    }
}
