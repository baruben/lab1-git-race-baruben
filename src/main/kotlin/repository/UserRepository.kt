package es.unizar.webeng.hello.repository

import es.unizar.webeng.hello.entity.User
import es.unizar.webeng.hello.enum.Role
import org.springframework.data.jpa.repository.JpaRepository


/**
 * Repository interface for managing [User] entities.
 *
 * Provides methods for retrieving users by role or username.
 */
interface UserRepository : JpaRepository<User, Long> {

    /**
     * Finds a user by their [Role].
     *
     * @param role The role to search for.
     * @return A [User] with the given role, or `null` if none exists.
     */
    fun findByRole(role: Role): User?

    /**
     * Finds a user by their unique username.
     *
     * @param username The username to search for.
     * @return A [User] with the given username, or `null` if none exists.
     */    
    fun findByUsername(username: String): User?
}
