package es.unizar.webeng.hello.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

class AuthenticationControllerUnitTests {

    private val controller = AuthenticationApiController()

    @Test
    fun `should return Not authenticated when auth is null`() {
        val result = controller.whoamiApi(null)

        assertThat(result).isEqualTo("Not authenticated")
    }

    @Test
    fun `should return authentication details when auth is provided`() {
        val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
        val auth = UsernamePasswordAuthenticationToken("test", null, authorities)

        val result = controller.whoamiApi(auth)

        assertThat(result).isEqualTo(
            "Auth type: UsernamePasswordAuthenticationToken, name: test, authorities: [ROLE_USER]"
        )
    }
}
