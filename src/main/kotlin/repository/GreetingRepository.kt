package es.unizar.webeng.hello.repository

import es.unizar.webeng.hello.entity.Greeting
import es.unizar.webeng.hello.entity.User
import es.unizar.webeng.hello.enum.Endpoint
import es.unizar.webeng.hello.enum.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.domain.Pageable


/**
 * Repository interface for managing [Greeting] entities.
 *
 * Provides methods for retrieving greetings and recovering statistics.
 */
interface GreetingRepository : JpaRepository<Greeting, Long> {

    /**
     * Retrieves all greetings sent by the given [User].
     *
     * @return A list of [Greeting] ordered, ordered by timestamp descending
     * (newest first).
     */
    fun findAllByUserOrderByTimestampDesc(user: User): List<Greeting>
    
    /**
     * Counts greetings grouped by [Endpoint].
     *
     * @return A list of ([Endpoint], count) pairs ordered by frequency descending.
     */
    @Query("""SELECT g.endpoint, COUNT(g) 
            FROM Greeting g
            GROUP BY g.endpoint
            ORDER BY COUNT(g) DESC""")
    fun countPerEndpoint(): List<Pair<Endpoint, Long>>

    /**
     * Counts greetings grouped by [Role] of the user who sent them.
     *
     * @return A list of ([Role], count) pairs ordered by frequency descending.
     */
    @Query("""SELECT g.user.role, COUNT(g) 
            FROM Greeting g 
            GROUP BY g.user.role
            ORDER BY COUNT(g) DESC""")
    fun countPerUserRole(): List<Pair<Role, Long>>
    
    /**
     * Counts greetings grouped by the [TimeOfDay] they were sent in.
     *
     * @return A list of ([TimeOfDay.name], count) pairs ordered by frequency descending.
     */
    @Query("""SELECT 
                CASE 
                    WHEN FUNCTION('HOUR', g.timestamp) BETWEEN 5 AND 11 THEN 'MORNING'
                    WHEN FUNCTION('HOUR', g.timestamp) BETWEEN 12 AND 17 THEN 'AFTERNOON'
                    ELSE 'NIGHT'
                END, 
                COUNT(g)
            FROM Greeting g
            GROUP BY 
                CASE 
                    WHEN FUNCTION('HOUR', g.timestamp) BETWEEN 5 AND 11 THEN 'MORNING'
                    WHEN FUNCTION('HOUR', g.timestamp) BETWEEN 12 AND 17 THEN 'AFTERNOON'
                    ELSE 'NIGHT'
                END
            ORDER BY COUNT(g) DESC""")
    fun countPerTimeOfDay(): List<Pair<String, Long>>
    
    /**
     * Counts greetings grouped by the date (formatted as yyyy-MM-dd) they were sent in.
     *
     * @param pageable Limits the number of results (top N dates).
     * @return A list of (date, count) pairs ordered by frequency descending.
     */
    @Query("""SELECT FUNCTION('FORMATDATETIME', g.timestamp, 'yyyy-MM-dd'), COUNT(g)
            FROM Greeting g
            GROUP BY FUNCTION('FORMATDATETIME', g.timestamp, 'yyyy-MM-dd')
            ORDER BY COUNT(g) DESC""")
    fun countPerDate(pageable: Pageable): List<Pair<String, Long>>
    
    /**
     * Counts greetings grouped by the hour of the day (0–23) they were sent in.
     *
     * @param pageable Limits the number of results (top N hours).
     * @return A list of (hour, count) pairs ordered by frequency descending.
     */
    @Query("""SELECT FUNCTION('HOUR', g.timestamp), COUNT(g) 
            FROM Greeting g 
            GROUP BY FUNCTION('HOUR', g.timestamp)
            ORDER BY COUNT(g) DESC""")
    fun countPerHour(pageable: Pageable): List<Pair<Int, Long>>

    /**
     * Counts greetings grouped by normalized (uppercase) name.
     *
     * @param pageable Limits the number of results (e.g., top N names).
     * @return A list of (name, count) pairs ordered by frequency descending.
     */
    @Query("""SELECT UPPER(g.name), COUNT(g) 
            FROM Greeting g
            GROUP BY UPPER(g.name)  
            ORDER BY COUNT(g) DESC""")
    fun countPerName(pageable: Pageable): List<Pair<String, Long>>

    /**
     * Counts the number of distinct normalized (uppercase) names across all greetings.
     *
     * @return Number of distinct names.
     */
    @Query("""SELECT COUNT(DISTINCT UPPER(g.name)) 
            FROM Greeting g""")            
    fun countDistinctNames(): Long

    /**
     * Counts the total number of greetings sent by the given [User].
     * 
     * @param user [User] used to filter.
     * @return Number of greetings.
     */
    @Query("""SELECT COUNT(g) 
            FROM Greeting g
            WHERE g.user = :user""")
    fun countByUser(@Param("user") user: User): Long

    /**
     * Counts greetings grouped by [Endpoint], filtered by the given [user].
     *
     * @param user [User] used to filter.
     * @return A list of ([Endpoint], count) pairs ordered by frequency descending.      
     */
    @Query("""SELECT g.endpoint, COUNT(g) 
            FROM Greeting g 
            WHERE g.user = :user
            GROUP BY g.endpoint
            ORDER BY COUNT(g) DESC""")
    fun countPerEndpointByUser(@Param("user") user: User): List<Pair<Endpoint, Long>>

    /**
     * Counts greetings grouped by the [TimeOfDay] they were sent in,
     * filtered by the given [User].
     *
     * @param user [User] used to filter.
     * @return A list of ([TimeOfDay.name], count) pairs ordered by frequency descending.
     */
    @Query("""SELECT 
                CASE 
                    WHEN FUNCTION('HOUR', g.timestamp) BETWEEN 5 AND 11 THEN 'MORNING'
                    WHEN FUNCTION('HOUR', g.timestamp) BETWEEN 12 AND 17 THEN 'AFTERNOON'
                    ELSE 'NIGHT'
                END, 
                COUNT(g)
            FROM Greeting g
            WHERE g.user = :user
            GROUP BY 
                CASE 
                    WHEN FUNCTION('HOUR', g.timestamp) BETWEEN 5 AND 11 THEN 'MORNING'
                    WHEN FUNCTION('HOUR', g.timestamp) BETWEEN 12 AND 17 THEN 'AFTERNOON'
                    ELSE 'NIGHT'
                END
            ORDER BY COUNT(g) DESC""")
    fun countPerTimeOfDayByUser(@Param("user") user: User): List<Pair<String, Long>>

    /**
     * Counts greetings grouped by the date (formatted as yyyy-MM-dd) they were sent in,
     * filtered by the given [User].
     *
     * @param user [User] used to filter.
     * @param pageable Limits the number of results (top N dates).
     * @return A list of (date, count) pairs ordered by frequency descending.
     */
    @Query("""SELECT FUNCTION('FORMATDATETIME', g.timestamp, 'yyyy-MM-dd'), COUNT(g)
            FROM Greeting g
            WHERE g.user = :user
            GROUP BY FUNCTION('FORMATDATETIME', g.timestamp, 'yyyy-MM-dd')
            ORDER BY COUNT(g) DESC""")
    fun countPerDateByUser(@Param("user") user: User, pageable: Pageable): List<Pair<String, Long>>

    /**
     * Counts greetings grouped by the hour of the day (0–23) they were sent in,
     * filtered by the given [User].
     *
     * @param user [User] used to filter.
     * @param pageable Limits the number of results (top N hours).
     * @return A list of (hour, count) pairs ordered by frequency descending.
     */
    @Query("""SELECT FUNCTION('HOUR', g.timestamp), COUNT(g) 
            FROM Greeting g 
            WHERE g.user = :user
            GROUP BY FUNCTION('HOUR', g.timestamp)
            ORDER BY COUNT(g) DESC""")
    fun countPerHourByUser(@Param("user") user: User, pageable: Pageable): List<Pair<Int, Long>>

    /**
     * Counts greetings grouped by normalized (uppercase) name,
     * filtered by the given [User].
     *
     * @param user [User] used to filter.
     * @param pageable Limits the number of results (e.g., top N names).
     * @return A list of (name, count) pairs ordered by frequency descending.
     */
    @Query("""SELECT UPPER(g.name), COUNT(g) 
            FROM Greeting g 
            WHERE g.user = :user
            GROUP BY UPPER(g.name)  
            ORDER BY COUNT(g) DESC""")
    fun countPerNameByUser(@Param("user") user: User, pageable: Pageable): List<Pair<String, Long>>

    /**
     * Counts the number of distinct normalized (uppercase) names across all greetings,
     * filtered by the given [User].
     *
     * @param user [User] used to filter.
     * @return Number of distinct names.
     */
    @Query("""SELECT COUNT(DISTINCT UPPER(g.name)) 
            FROM Greeting g
            WHERE g.user = :user""")            
    fun countDistinctNamesByUser(@Param("user") user: User): Long
}
