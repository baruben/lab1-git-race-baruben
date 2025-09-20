package es.unizar.webeng.hello.response

data class TopNames(
    val name: String,
    val count: Long
)

data class TopDates(
    val date: String,
    val count: Long
)

data class TopHours(
    val hour: Int,
    val count: Long
)

data class RankingRole(
    val role: String,
    val count: Long
)

data class RankingEndpoint(
    val endpoint: String,
    val count: Long
)

data class RankingTimeOfDay(
    val timeOfDay: String,
    val count: Long
)

data class StatisticsResponse(
    val totalGreetings: Long,
    val rankingEndpoints: List<RankingEndpoint>,
    val rankingRoles: List<RankingRole>,
    val rankingTimesOfDay: List<RankingTimeOfDay>,
    val topDates: List<TopDates>,
    val topHours: List<TopHours>,
    val totalNames: Long,
    val topNames: List<TopNames>
)

data class MyStatisticsResponse(
    val totalGreetings: Long,
    val rankingEndpoints: List<RankingEndpoint>,
    val rankingTimesOfDay: List<RankingTimeOfDay>,
    val topDates: List<TopDates>,
    val topHours: List<TopHours>,
    val totalNames: Long,
    val topNames: List<TopNames>
)