package es.unizar.webeng.hello.service

import es.unizar.webeng.hello.repository.UserRepository
import es.unizar.webeng.hello.entity.SecurityUser
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service


/**
 * Service layer for loading user details required by Spring Security.
 *
 * Implements [UserDetailsService] to allow Spring Security to authenticate
 * users based on username.
 *
 * @property userRepository Repository used to fetch [User] entities.
 */
@Service
class SecurityUserService(
    private val userRepository: UserRepository
) : UserDetailsService {

    /**
     * Loads a user by their username.
     *
     * Called by Spring Security during authentication.
     *
     * @param username The username of the user to load.
     * @return A [UserDetails] object representing the authenticated user.
     * @throws UsernameNotFoundException If no user with the given username exists.
     */
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("User not found")
        return SecurityUser(user)
    }
}