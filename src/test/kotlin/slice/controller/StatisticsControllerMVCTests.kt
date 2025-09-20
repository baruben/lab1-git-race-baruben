package es.unizar.webeng.hello.controller

import es.unizar.webeng.hello.entity.User
import es.unizar.webeng.hello.enum.Role
import es.unizar.webeng.hello.filter.RateLimitFilter
import es.unizar.webeng.hello.response.*
import es.unizar.webeng.hello.service.StatisticsService
import es.unizar.webeng.hello.service.UserService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@WebMvcTest(StatisticsApiController::class)
@AutoConfigureMockMvc(addFilters = false)
class StatisticsControllerMvcTests() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var statisticsService: StatisticsService

    @MockBean
    lateinit var userService: UserService

    @MockBean
    private lateinit var rateLimitFilter: RateLimitFilter
    
    @Test
    fun `should return 200 and statistics API response as JSON`() {
        val expected = StatisticsResponse(
            totalGreetings = 10,
            rankingEndpoints = listOf(RankingEndpoint("WEB", 4)),
            rankingRoles = listOf(RankingRole("USER", 6)),
            rankingTimesOfDay = listOf(RankingTimeOfDay("MORNING", 5)),
            topDates = listOf(TopDates("2025-09-20", 3)),
            topHours = listOf(TopHours(10, 2)),
            totalNames = 7,
            topNames = listOf(TopNames("TEST", 3))
        )

        whenever(statisticsService.getStatistics()).thenReturn(expected)

        mockMvc.get("/api/statistics")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.totalGreetings") { value(10) }
                jsonPath("$.rankingEndpoints[0].endpoint") { value("WEB") }
                jsonPath("$.rankingEndpoints[0].count") { value(4) }
                jsonPath("$.rankingRoles[0].role") { value("USER") }
                jsonPath("$.rankingRoles[0].count") { value(6) }
                jsonPath("$.rankingTimesOfDay[0].timeOfDay") { value("MORNING") }
                jsonPath("$.rankingTimesOfDay[0].count") { value(5) }
                jsonPath("$.topDates[0].date") { value("2025-09-20") }
                jsonPath("$.topDates[0].count") { value(3) }
                jsonPath("$.topHours[0].hour") { value(10) }
                jsonPath("$.topHours[0].count") { value(2) }
                jsonPath("$.topNames[0].name") { value("TEST") }
                jsonPath("$.topNames[0].count") { value(3) }
                jsonPath("$.totalNames") { value(7) }
            }
    }

    @Test
    fun `should return 200 and myStatistics API response as JSON`() {
        val user = User(username = "test", password = "password", role = Role.USER)
        val expected = MyStatisticsResponse(
            totalGreetings = 5,
            rankingEndpoints = listOf(RankingEndpoint("API", 3)),
            rankingTimesOfDay = listOf(RankingTimeOfDay("AFTERNOON", 2)),
            topDates = listOf(TopDates("2025-09-20", 2)),
            topHours = listOf(TopHours(14, 1)),
            totalNames = 2,
            topNames = listOf(TopNames("TEST", 2))
        )

        whenever(userService.getSessionUser()).thenReturn(user)
        whenever(statisticsService.getMyStatistics(user)).thenReturn(expected)

        mockMvc.get("/api/myStatistics")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.totalGreetings") { value(5) }
                jsonPath("$.rankingEndpoints[0].endpoint") { value("API") }
                jsonPath("$.rankingEndpoints[0].count") { value(3) }
                jsonPath("$.rankingTimesOfDay[0].timeOfDay") { value("AFTERNOON") }
                jsonPath("$.rankingTimesOfDay[0].count") { value(2) }
                jsonPath("$.topDates[0].date") { value("2025-09-20") }
                jsonPath("$.topDates[0].count") { value(2) }
                jsonPath("$.topHours[0].hour") { value(14) }
                jsonPath("$.topHours[0].count") { value(1) }
                jsonPath("$.topNames[0].name") { value("TEST") }
                jsonPath("$.topNames[0].count") { value(2) }
                jsonPath("$.totalNames") { value(2) }
            }
    }
}
