package es.unizar.webeng.hello.service

import es.unizar.webeng.hello.entity.User
import es.unizar.webeng.hello.entity.SecurityUser
import es.unizar.webeng.hello.enum.Role
import es.unizar.webeng.hello.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import jakarta.annotation.PostConstruct
import java.util.UUID
import org.springframework.security.core.context.SecurityContextHolder


/**
 * Service for managing [User] entities and authentication-related operations.
 *
 * Handles user creation, retrieval, and session-based user management, encapsulating
 * the persistence logic provided by [UserRepository].
 *
 * @property userRepository Repository for performing CRUD operations on [User] entities.
 * @property passwordEncoder Password encoder for storing secure user passwords.
 */
@Service
class UserService(
    private val userRepository: UserRepository, 
    private val passwordEncoder: PasswordEncoder
) {

    /**
     * Default guest user used when no authenticated user is present.
     */
    lateinit var guest: User

    /**
     * Initializes the guest user after bean construction.
     *
     * If a user with role [Role.GUEST] exists, it is assigned to [guest].
     * Otherwise, a new guest user is created and persisted.
     */
    @PostConstruct
    fun init() {
        guest = userRepository.findByRole(Role.GUEST) ?: userRepository.save(
            User(username = "", password = "", role = Role.GUEST)
        )
    }

    /**
     * Creates a new [User] with the specified username and password.
     *
     * @param username Desired username for the new user.
     * @param password Plain-text password, which will be securely encoded.
     * @return The newly created and persisted [User].
     * @throws IllegalArgumentException If a user with the given username already exists.
     */
    fun create(username: String, password: String): User {
        if (userRepository.findByUsername(username) != null) {
            throw IllegalArgumentException("Username not valid")
        }
        
        val user = User(
            username = username,
            password = passwordEncoder.encode(password),
            role = Role.USER,
        )
        return userRepository.save(user)
    }

    /**
     * Finds a user by their username.
     *
     * @param username The username to search for.
     * @return The [User] if found, or `null` if none exists.
     */
    fun findByUsername(username: String): User? = userRepository.findByUsername(username)

    /**
     * Returns the currently authenticated user from the security context.
     *
     * If no authenticated user exists, returns the default [guest] user.
     *
     * @return The session [User], either authenticated or guest.
     */
    fun getSessionUser(): User {
        val auth = SecurityContextHolder.getContext().authentication

        return if (auth != null && auth.isAuthenticated && auth.principal is SecurityUser) {
            (auth.principal as SecurityUser).user
        } else {
            guest
        }
    }
}