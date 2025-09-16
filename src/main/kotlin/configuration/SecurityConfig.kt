package es.unizar.webeng.hello.configuration

import es.unizar.webeng.hello.service.SecurityUserService
import es.unizar.webeng.hello.repository.TokenRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

@Configuration
class SecurityConfig(
    private val securityUserService: SecurityUserService,
    private val tokenRepository: TokenRepository,
    @Value("\${security.rememberme.key}") private val rememberMeKey: String
) {

    @Bean
    fun authenticationManager(authConfig: AuthenticationConfiguration): AuthenticationManager {
        return authConfig.authenticationManager
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authProvider(): DaoAuthenticationProvider {
        val provider = DaoAuthenticationProvider()
        provider.setUserDetailsService(securityUserService)
        provider.setPasswordEncoder(passwordEncoder())
        return provider
    }

    @Bean
    fun customRememberMeServices(): PersistentTokenBasedRememberMeServices =
        PersistentTokenBasedRememberMeServices(
            rememberMeKey,
            securityUserService,
            tokenRepository
        ).apply {
            setAlwaysRemember(true)
            setTokenValiditySeconds(7 * 24 * 60 * 60) // 7 days
        }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests {
                it.requestMatchers("/", "/signup", "/login", "/api/whoami", "/api/hello", "/actuator/health",
                    "/css/**", "/js/**", "/images/**", "/assets/**", "/webjars/**", "/vendor/**", 
                    "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**")
                    .permitAll()
                it.anyRequest().hasRole("USER")
            }
            .formLogin {
                it.loginPage("/login").permitAll()
                it.defaultSuccessUrl("/", true)
            }
            .logout {
                it.logoutUrl("/logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "remember-me")
                .permitAll()
            }
            .rememberMe { rememberMe ->
                rememberMe.rememberMeServices(customRememberMeServices())
            }

        return http.build()
    }

    @Bean
    fun webSecurityCustomizer() = WebSecurityCustomizer {
        it.ignoring().requestMatchers("/h2-console/**")
    }
}
