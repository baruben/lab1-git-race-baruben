package es.unizar.webeng.hello.service

import es.unizar.webeng.hello.entity.SecurityUser
import es.unizar.webeng.hello.entity.User
import es.unizar.webeng.hello.enum.Role
import es.unizar.webeng.hello.repository.UserRepository
import es.unizar.webeng.hello.service.SecurityUserService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.security.core.userdetails.UsernameNotFoundException

class SecurityUserServiceTests {

    private lateinit var userRepository: UserRepository
    private lateinit var securityUserService: SecurityUserService

    @BeforeEach
    fun setup() {
        userRepository = mock()
        securityUserService = SecurityUserService(userRepository)
    }

    @Test
    fun `should return SecurityUser when user exists`() {
        val user = User(username = "test", password = "password", role = Role.USER)
        whenever(userRepository.findByUsername("test")).thenReturn(user)

        val result = securityUserService.loadUserByUsername("test")

        assertThat(result).isInstanceOf(SecurityUser::class.java)
        assertThat(result.username).isEqualTo("test")
    }

    @Test
    fun `should throw UsernameNotFoundException when user does NOT exist`() {
        whenever(userRepository.findByUsername("test")).thenReturn(null)

        org.junit.jupiter.api.assertThrows<UsernameNotFoundException> {
            securityUserService.loadUserByUsername("test")
        }
    }
}
