package es.unizar.webeng.hello.controller

import es.unizar.webeng.hello.entity.User
import es.unizar.webeng.hello.enum.Role
import es.unizar.webeng.hello.response.*
import es.unizar.webeng.hello.service.StatisticsService
import es.unizar.webeng.hello.service.UserService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class StatisticsControllerUnitTests {

    private lateinit var statisticsService: StatisticsService

    private lateinit var userService: UserService

    private lateinit var controller: StatisticsApiController

    @BeforeEach
    fun setup() {
        statisticsService = mock()
        userService = mock()
        controller = StatisticsApiController(statisticsService, userService)
    }

    @Test
    fun `should return StatisticsResponse`() {
        val expected = StatisticsResponse(
            totalGreetings = 10,
            rankingEndpoints = listOf(RankingEndpoint("WEB", 4)),
            rankingRoles = listOf(RankingRole("USER", 6)),
            rankingTimesOfDay = listOf(RankingTimeOfDay("MORNING", 5)),
            topDates = listOf(TopDates("2025-09-20", 3)),
            topHours = listOf(TopHours(10, 2)),
            totalNames = 7,
            topNames = listOf(TopNames("ALICE", 3))
        )

        whenever(statisticsService.getStatistics()).thenReturn(expected)

        val result = controller.statisticsApi()

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `should return MyStatisticsResponse`() {
        val user = User(username = "test", password = "password", role = Role.USER)
        val expected = MyStatisticsResponse(
            totalGreetings = 5,
            rankingEndpoints = listOf(RankingEndpoint("API", 3)),
            rankingTimesOfDay = listOf(RankingTimeOfDay("AFTERNOON", 2)),
            topDates = listOf(TopDates("2025-09-20", 2)),
            topHours = listOf(TopHours(14, 1)),
            totalNames = 2,
            topNames = listOf(TopNames("BOB", 2))
        )

        whenever(userService.getSessionUser()).thenReturn(user)
        whenever(statisticsService.getMyStatistics(user)).thenReturn(expected)

        val result = controller.myStatisticsApi()

        assertThat(result).isEqualTo(expected)
    }
}
