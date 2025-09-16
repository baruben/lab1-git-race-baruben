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

@Service
class UserService(
    private val userRepository: UserRepository, 
    private val passwordEncoder: PasswordEncoder
) {
    lateinit var guest: User

    @PostConstruct
    fun init() {
        guest = userRepository.findByRole(Role.GUEST) ?: userRepository.save(
            User(username = "", password = "", role = Role.GUEST)
        )
    }

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

    fun findByUsername(username: String): User? = userRepository.findByUsername(username)

    fun getSessionUser(): User {
        val auth = SecurityContextHolder.getContext().authentication

        return if (auth != null && auth.isAuthenticated && auth.principal is SecurityUser) {
            (auth.principal as SecurityUser).user
        } else {
            guest
        }
    }
}