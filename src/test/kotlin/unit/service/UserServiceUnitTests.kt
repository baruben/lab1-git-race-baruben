package es.unizar.webeng.hello.service

import es.unizar.webeng.hello.entity.User
import es.unizar.webeng.hello.entity.SecurityUser
import es.unizar.webeng.hello.enum.Role
import es.unizar.webeng.hello.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

class UserServiceTests {

    private lateinit var userRepository: UserRepository
    private lateinit var passwordEncoder: PasswordEncoder
    private lateinit var userService: UserService

    private val guest = User(username = "", password = "", role = Role.GUEST)

    @BeforeEach
    fun setup() {
        userRepository = mock()
        passwordEncoder = mock()
        userService = UserService(userRepository, passwordEncoder)
    }

    @Test
    fun `should save encoded password`() {
        whenever(userRepository.findByUsername("test")).thenReturn(null)
        whenever(passwordEncoder.encode("password")).thenReturn("encodedPassword")
        val expected = User(username = "test", password = "encodedPassword", role = Role.USER)
        whenever(userRepository.save(any())).thenReturn(expected)

        val result = userService.create("test", "password")

        verify(userRepository).save(check {
            assertThat(it.username).isEqualTo("test")
            assertThat(it.password).isEqualTo("encodedPassword")
            assertThat(it.role).isEqualTo(Role.USER)
        })
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `should throw exception if username already exists`() {
        whenever(userRepository.findByUsername("test")).thenReturn(User(username = "test", password = "password", role = Role.USER))

        org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            userService.create("test", "secret")
        }
    }

    @Test
    fun `should return GUEST if no user is authenticated`() {
        SecurityContextHolder.getContext().authentication = null
        userService.guest = guest

        val result = userService.getSessionUser()

        assertThat(result).isEqualTo(guest)
    }

    @Test
    fun `should return authenticated user`() {
        val user = User(username = "test", password = "password", role = Role.USER)
        val securityUser = SecurityUser(user)
        val authentication = UsernamePasswordAuthenticationToken(securityUser, null, emptyList())
        SecurityContextHolder.getContext().authentication = authentication
        userService.guest = guest

        val result = userService.getSessionUser()

        assertThat(result).isEqualTo(user)
    }
}
