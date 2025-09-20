package es.unizar.webeng.hello.repository

import es.unizar.webeng.hello.entity.Greeting
import es.unizar.webeng.hello.entity.User
import es.unizar.webeng.hello.enum.Endpoint
import es.unizar.webeng.hello.enum.Role
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.domain.Pageable

interface GreetingRepository : JpaRepository<Greeting, Long> {
    fun findAllByUserOrderByTimestampDesc(user: User): List<Greeting>

    @Query("""SELECT g.endpoint, COUNT(g) 
            FROM Greeting g
            GROUP BY g.endpoint
            ORDER BY COUNT(g) DESC""")
    fun countPerEndpoint(): List<Pair<Endpoint, Long>>

    @Query("""SELECT g.user.role, COUNT(g) 
            FROM Greeting g 
            GROUP BY g.user.role
            ORDER BY COUNT(g) DESC""")
    fun countPerUserRole(): List<Pair<Role, Long>>

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

    @Query("""SELECT FUNCTION('FORMATDATETIME', g.timestamp, 'yyyy-MM-dd'), COUNT(g)
            FROM Greeting g
            GROUP BY FUNCTION('FORMATDATETIME', g.timestamp, 'yyyy-MM-dd')
            ORDER BY COUNT(g) DESC""")
    fun countPerDate(pageable: Pageable): List<Pair<String, Long>>

    @Query("""SELECT FUNCTION('HOUR', g.timestamp), COUNT(g) 
            FROM Greeting g 
            GROUP BY FUNCTION('HOUR', g.timestamp)
            ORDER BY COUNT(g) DESC""")
    fun countPerHour(pageable: Pageable): List<Pair<Int, Long>>

    @Query("""SELECT UPPER(g.name), COUNT(g) 
            FROM Greeting g
            GROUP BY UPPER(g.name)  
            ORDER BY COUNT(g) DESC""")
    fun countPerName(pageable: Pageable): List<Pair<String, Long>>

    @Query("""SELECT COUNT(DISTINCT UPPER(g.name)) 
            FROM Greeting g""")            
    fun countDistinctNames(): Long

    @Query("""SELECT COUNT(g) 
            FROM Greeting g
            WHERE g.user = :user""")
    fun countByUser(@Param("user") user: User): Long

    @Query("""SELECT g.endpoint, COUNT(g) 
            FROM Greeting g 
            WHERE g.user = :user
            GROUP BY g.endpoint
            ORDER BY COUNT(g) DESC""")
    fun countPerEndpointByUser(@Param("user") user: User): List<Pair<Endpoint, Long>>

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

    @Query("""SELECT FUNCTION('FORMATDATETIME', g.timestamp, 'yyyy-MM-dd'), COUNT(g)
            FROM Greeting g
            WHERE g.user = :user
            GROUP BY FUNCTION('FORMATDATETIME', g.timestamp, 'yyyy-MM-dd')
            ORDER BY COUNT(g) DESC""")
    fun countPerDateByUser(@Param("user") user: User, pageable: Pageable): List<Pair<String, Long>>

    @Query("""SELECT FUNCTION('HOUR', g.timestamp), COUNT(g) 
            FROM Greeting g 
            WHERE g.user = :user
            GROUP BY FUNCTION('HOUR', g.timestamp)
            ORDER BY COUNT(g) DESC""")
    fun countPerHourByUser(@Param("user") user: User, pageable: Pageable): List<Pair<Int, Long>>

    @Query("""SELECT UPPER(g.name), COUNT(g) 
            FROM Greeting g 
            WHERE g.user = :user
            GROUP BY UPPER(g.name)  
            ORDER BY COUNT(g) DESC""")
    fun countPerNameByUser(@Param("user") user: User, pageable: Pageable): List<Pair<String, Long>>

    @Query("""SELECT COUNT(DISTINCT UPPER(g.name)) 
            FROM Greeting g
            WHERE g.user = :user""")            
    fun countDistinctNamesByUser(@Param("user") user: User): Long
}
