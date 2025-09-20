package es.unizar.webeng.hello.entity

import es.unizar.webeng.hello.enum.Role
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SecurityUserTests {

    private val user = User(username = "test", password = "password", role = Role.USER)
    private val securityUser = SecurityUser(user)

    @Test
    fun `should return security user's ROLE`() {
        val authorities = securityUser.authorities
        assertThat(authorities).hasSize(1)
        assertThat(authorities.first().authority).isEqualTo("ROLE_USER")
    }

    @Test
    fun `should return underlying user's username`() {
        assertThat(securityUser.username).isEqualTo("test")
    }

    @Test
    fun `should return underlying user's password`() {
        assertThat(securityUser.password).isEqualTo("password")
    }

    @Test
    fun `account status methods should return true`() {
        assertThat(securityUser.isAccountNonExpired).isTrue
        assertThat(securityUser.isAccountNonLocked).isTrue
        assertThat(securityUser.isCredentialsNonExpired).isTrue
        assertThat(securityUser.isEnabled).isTrue
    }
}
