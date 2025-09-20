package es.unizar.webeng.hello.service

import es.unizar.webeng.hello.response.*
import es.unizar.webeng.hello.entity.User
import es.unizar.webeng.hello.repository.GreetingRepository
import org.springframework.stereotype.Service
import org.springframework.data.domain.PageRequest
import org.springframework.beans.factory.annotation.Value

@Service
class StatisticsService(
    private val greetingRepository: GreetingRepository,
    @param:Value("\${top.limit:3}") private val limit: Int
) {

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