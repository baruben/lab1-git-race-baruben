package es.unizar.webeng.hello.configuration

import es.unizar.webeng.hello.service.SecurityUserService
import es.unizar.webeng.hello.repository.TokenRepository
import es.unizar.webeng.hello.filter.RateLimitFilter
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


/**
 * Spring Security configuration.
 *
 * Configures authentication, authorization, remember-me services, and rate limiting.
 *
 * ## Features
 * - **Authentication**
 *   - Uses [SecurityUserService] to load users.
 *   - Passwords encoded with [BCryptPasswordEncoder].
 *   - DaoAuthenticationProvider is configured with the user service and password encoder.
 *
 * - **Remember-Me**
 *   - [PersistentTokenBasedRememberMeServices] backed by [TokenRepository].
 *   - Key from application properties (`security.rememberme.key`).
 *   - Tokens valid for 7 days.
 *   - Always remember enabled.
 *
 * - **Authorization**
 *   - Public endpoints: "/", "/signup", "/login", "/api/whoami", "/api/hello", "/api/statistics", 
 *     static resources, actuator health, Swagger UI, etc.
 *   - All other requests require role `"USER"`.
 *
 * - **Form Login**
 *   - Custom login page at `/login`.
 *   - Redirects to `/` on successful login.
 *
 * - **Logout**
 *   - URL: `/logout`.
 *   - Invalidates session and deletes cookies `JSESSIONID` and `remember-me`.
 *
 * - **Rate Limiting**
 *   - [RateLimitFilter] applied before [UsernamePasswordAuthenticationFilter].
 */
@Configuration
class SecurityConfig(
    private val securityUserService: SecurityUserService,
    private val tokenRepository: TokenRepository,
    private val rateLimitFilter: RateLimitFilter,
    @param:Value("\${security.rememberme.key}") private val rememberMeKey: String
) {

    /**
     * Exposes [AuthenticationManager] as a bean for authentication purposes.
     */
    @Bean
    fun authenticationManager(authConfig: AuthenticationConfiguration): AuthenticationManager {
        return authConfig.authenticationManager
    }

    /**
     * Password encoder bean using BCrypt hashing.
     */
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    /**
     * Configures DAO authentication provider with user details service and password encoder.
     */
    @Bean
    fun authProvider(): DaoAuthenticationProvider {
        val provider = DaoAuthenticationProvider()
        provider.setUserDetailsService(securityUserService)
        provider.setPasswordEncoder(passwordEncoder())
        return provider
    }

    /**
     * Configures persistent remember-me services using [TokenRepository].
     */
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

    /**
     * Configures the main security filter chain.
     *
     * - Applies authorization rules
     * - Configures form login
     * - Configures logout
     * - Configures remember-me
     * - Adds [RateLimitFilter] before username/password authentication
     */
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests {
                it.requestMatchers("/", "/signup", "/login", "/api/whoami", "/api/hello", "/api/statistics", 
                    "/css/**", "/js/**", "/images/**", "/assets/**", "/webjars/**", "/vendor/**", 
                    "/actuator/health", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**")
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

        http.addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    /**
     * Configures paths that Spring Security should ignore (e.g., H2 console).
     */
    @Bean
    fun webSecurityCustomizer() = WebSecurityCustomizer {
        it.ignoring().requestMatchers("/h2-console/**")
    }
}