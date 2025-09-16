package es.unizar.webeng.hello.repository

import es.unizar.webeng.hello.entity.Greeting
import es.unizar.webeng.hello.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface GreetingRepository : JpaRepository<Greeting, Long> {
    fun findAllByUserOrderByTimestampDesc(user: User): List<Greeting>
}
