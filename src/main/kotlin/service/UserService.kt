package es.unizar.webeng.hello.service

import es.unizar.webeng.hello.entity.User
import es.unizar.webeng.hello.enum.Role
import es.unizar.webeng.hello.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import jakarta.annotation.PostConstruct
import java.util.UUID

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
            throw IllegalArgumentException("Username already exists")
        }
        
        val user = User(
            username = username,
            password = passwordEncoder.encode(password),
            role = Role.USER,
        )
        return userRepository.save(user)
    }

    fun findByUsername(username: String): User? = userRepository.findByUsername(username)
}