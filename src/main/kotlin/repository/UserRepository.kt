package es.unizar.webeng.hello.repository

import es.unizar.webeng.hello.data.User
import es.unizar.webeng.hello.data.UserType
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserRepository : JpaRepository<User, UUID> {
    fun findUserByUserType(userType: UserType): User?
    fun findUserByUsername(username: String): User?
}
