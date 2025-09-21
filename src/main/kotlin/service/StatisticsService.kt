package es.unizar.webeng.hello.service

import es.unizar.webeng.hello.response.*
import es.unizar.webeng.hello.entity.User
import es.unizar.webeng.hello.repository.GreetingRepository
import org.springframework.stereotype.Service
import org.springframework.data.domain.PageRequest
import org.springframework.beans.factory.annotation.Value


/**
 * Service layer for generating greeting statistics for all users or a specific user.
 *
 * Computes totals, rankings, and top items such as endpoints, roles, times of day, dates,
 * hours, and names.
 *
 * @property greetingRepository Repository for querying greeting data.
 * @property limit Maximum number of top items to return (default 3, configurable via `top.limit`).
 */
@Service
class StatisticsService(
    private val greetingRepository: GreetingRepository,
    @param:Value("\${top.limit:3}") private val limit: Int
) {

    /**
     * Generates statistics across all greetings in the system.
     *
     * @return [StatisticsResponse] containing overall counts and rankings.
     */
    fun getStatistics(): StatisticsResponse {
        val totalGreetings = greetingRepository.count()

        val rankingEndpoints = greetingRepository.countPerEndpoint()
            .map { RankingEndpoint(it.first.name, it.second) }

        val rankingRoles = greetingRepository.countPerUserRole()
            .map { RankingRole(it.first.name, it.second) }

        val rankingTimesOfDay = greetingRepository.countPerTimeOfDay()
            .map { RankingTimeOfDay(it.first, it.second) }

        val topDates = greetingRepository.countPerDate(PageRequest.of(0, limit))
            .map { TopDates(it.first, it.second) }

        val topHours = greetingRepository.countPerHour(PageRequest.of(0, limit))
            .map { TopHours(it.first, it.second) }

        val totalNames = greetingRepository.countDistinctNames()

        val topNames = greetingRepository.countPerName(PageRequest.of(0, limit))
            .map { TopNames(it.first, it.second) }

        return StatisticsResponse(
            totalGreetings = totalGreetings,
            rankingEndpoints = rankingEndpoints,
            rankingRoles = rankingRoles,
            rankingTimesOfDay = rankingTimesOfDay,
            topDates = topDates,
            topHours = topHours,
            totalNames = totalNames,
            topNames = topNames
        )
    }

    /**
     * Generates statistics only for a specific [User].
     *
     * @param user The [User] for whom the statistics are generated.
     * @return [MyStatisticsResponse] containing counts and rankings filtered by the user.
     */
    fun getMyStatistics(user: User): MyStatisticsResponse {
        val totalGreetings = greetingRepository.countByUser(user)

        val rankingEndpoints = greetingRepository.countPerEndpointByUser(user)
            .map { RankingEndpoint(it.first.name, it.second) }

        val rankingTimesOfDay = greetingRepository.countPerTimeOfDayByUser(user)
            .map { RankingTimeOfDay(it.first, it.second) }

        val topDates = greetingRepository.countPerDateByUser(user, PageRequest.of(0, limit))
            .map { TopDates(it.first, it.second) }

        val topHours = greetingRepository.countPerHourByUser(user, PageRequest.of(0, limit))
            .map { TopHours(it.first, it.second) }

        val topNames = greetingRepository.countPerNameByUser(user, PageRequest.of(0, limit))
            .map { TopNames(it.first, it.second) }

        val totalNames = greetingRepository.countDistinctNamesByUser(user)
            

        return MyStatisticsResponse(
            totalGreetings = totalGreetings,
            rankingEndpoints = rankingEndpoints,
            rankingTimesOfDay = rankingTimesOfDay,
            topDates = topDates,
            topHours = topHours,
            totalNames = totalNames,
            topNames = topNames
        )
    }
}