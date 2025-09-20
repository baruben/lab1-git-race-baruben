package es.unizar.webeng.hello.response


/**
 * Represents the top names that have appeared most times in greeting messages.
 *
 * @property name The name.
 * @property count How many times this name has appeared in greetings.
 */
data class TopNames(
    val name: String,
    val count: Long
)


/**
 * Represents the top dates in which most greeting messages have be sent.
 *
 * @property date The date formatted as a String ("YYYY-MM-DD").
 * @property count Number of greetings on that date.
 */
data class TopDates(
    val date: String,
    val count: Long
)


/**
 * Represents the top hours in which most greeting messages have be sent.
 *
 * @property date The hour.
 * @property count Number of greetings on that hour.
 */
data class TopHours(
    val hour: Int,
    val count: Long
)


/**
 * Represents the ranking of user [Role] ordered by greeting messages sent.
 *
 * @property date The role formatted as a String.
 * @property count Number of greetings sent by that [Role].
 */
data class RankingRole(
    val role: String,
    val count: Long
)


/**
 * Represents the ranking of [Endpoint] ordered by greeting messages sent from it.
 *
 * @property date The [Endpoint] formatted as a String.
 * @property count Number of greetings sent from that [Endpoint].
 */
data class RankingEndpoint(
    val endpoint: String,
    val count: Long
)


/**
 * Represents the ranking of [TimeOfDay] ordered by greeting messages sent in it.
 *
 * @property date The [TimeOfDay] formatted as a String.
 * @property count Number of greetings sent in that [TimeOfDay].
 */
data class RankingTimeOfDay(
    val timeOfDay: String,
    val count: Long
)


/**
 * Represents the response of the API endpoint that recovers aggregated statistics 
 * about greetings across all users.
 *
 * @property totalGreetings Total number of greetings sent.
 * @property rankingEndpoints List of [RankingEndpoint] entries.
 * @property rankingRoles List of [RankingRole] entries.
 * @property rankingTimesOfDay List of [RankingTimeOfDay] entries.
 * @property topDates List of [TopDates] entries.
 * @property topHours List of [TopHours] entries.
 * @property totalNames Total number of unique names greeted.
 * @property topNames List of [TopNames] entries.
 */
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


/**
 * Represents the response of the API endpoint that recovers aggregated statistics 
 * about greetings for an authenticated user.
 *
 * @property totalGreetings Total number of greetings sent by the user.
 * @property rankingEndpoints List of [RankingEndpoint] entries for the user.
 * @property rankingTimesOfDay List of [RankingTimeOfDay] entries for the user.
 * @property topDates List of [TopDates] entries for the user.
 * @property topHours List of [TopHours] entries for the user.
 * @property totalNames Total number of unique names greeted by the user.
 * @property topNames List of [TopNames] entries for the user.
 */
data class MyStatisticsResponse(
    val totalGreetings: Long,
    val rankingEndpoints: List<RankingEndpoint>,
    val rankingTimesOfDay: List<RankingTimeOfDay>,
    val topDates: List<TopDates>,
    val topHours: List<TopHours>,
    val totalNames: Long,
    val topNames: List<TopNames>
)