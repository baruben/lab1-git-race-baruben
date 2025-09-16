package es.unizar.webeng.hello.repository

import es.unizar.webeng.hello.entity.User
import es.unizar.webeng.hello.enum.Role
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByRole(role: Role): User?
    fun findByUsername(username: String): User?
}
