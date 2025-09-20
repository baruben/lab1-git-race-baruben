package es.unizar.webeng.hello.entity

import es.unizar.webeng.hello.enum.Role
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails


/**
 * Wraps a [User] entity to provide Spring Security with authentication and authorization information.
 *
 * Implements [UserDetails] to integrate with Spring Security's authentication system.
 *
 * @property user The [User] entity being wrapped.
 */
class SecurityUser(val user: User) : UserDetails {

    /**
     * Returns the authorities granted to the user.
     *
     * Converts the user's [Role] into a Spring Security [GrantedAuthority].
     */
    override fun getAuthorities(): Collection<GrantedAuthority> =
        listOf(SimpleGrantedAuthority("ROLE_${user.role.name}"))

    /**
     * Returns the user's password.
     */
    override fun getPassword() = user.password

    /**
     * Returns the user's username.
     */
    override fun getUsername() = user.username

    /**
     * Indicates whether the user's account has expired.
     *
     * Always returns `true` since this implementation does not track account expiration.
     */
    override fun isAccountNonExpired() = true

    /**
     * Indicates whether the user is locked or unlocked.
     *
     * Always returns `true` since this implementation does not track account locking.
     */
    override fun isAccountNonLocked() = true

    /**
     * Indicates whether the user's credentials (password) have expired.
     *
     * Always returns `true` since this implementation does not track credential expiration.
     */
    override fun isCredentialsNonExpired() = true

    /**
     * Indicates whether the user is enabled or disabled.
     *
     * Always returns `true` since this implementation does not track user enablement.
     */
    override fun isEnabled() = true
}