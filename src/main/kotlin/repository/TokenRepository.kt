package es.unizar.webeng.hello.repository

import es.unizar.webeng.hello.entity.PersistentLogin
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.ZoneId


/**
 * Implementation of [PersistentTokenRepository] sed by Spring Security to handle
 * "remember me" authentication tokens.
 *
 * Stores tokens in the database via the [PersistentLogin] entity and provides
 * methods to create, update, retrieve, and remove tokens.
 *
 * ---
 *
 * ## Remember-Me Authentication Flow:
 *
 * 1. **User logs in with "remember me" enabled**
 *    - Spring Security calls [createNewToken].
 *    - A new [PersistentLogin] entity is saved with series, token, and lastUsed timestamp.
 *    - A cookie containing (series, token) is sent to the browser.
 *
 * 2. **User returns with a valid remember-me cookie**
 *    - Spring Security extracts (series, token) from the cookie.
 *    - Calls [getTokenForSeries].
 *    - If series exists:
 *        - Validates token against the stored one.
 *        - If valid → user is authenticated.
 *        - Calls [updateToken] to refresh token value and lastUsed timestamp.
 *    - If invalid → cookie is rejected, authentication fails.
 *
 * 3. **User logs out**
 *    - Spring Security calls [removeUserTokens].
 *    - All [PersistentLogin] records for the user are deleted.
 *    - Remember-me cookie is cleared from the browser.
 *
 * ---
 *
 * ## Example flow:
 * ```
 * User logs in with "Remember Me"
 *   ↳ [createNewToken] saves PersistentLogin + sets cookie
 *
 * User returns with remember-me cookie
 *   ↳ [getTokenForSeries] loads PersistentLogin
 *       ↳ If token matches → [updateToken] refreshes + auth success
 *       ↳ If token invalid → cookie rejected, re-login required
 *
 * User logs out
 *   ↳ [removeUserTokens] deletes all PersistentLogin + clears cookie
 * ```
 *
 * @property persistentLoginRepository Repository for managing [PersistentLogin] records.
 */
@Component
class TokenRepository(
    private val persistentLoginRepository: PersistentLoginRepository
) : PersistentTokenRepository {

    /**
     * Creates a new persistent login token.
     *
     * Called when a user logs in with "remember me" enabled.
     *
     * @param token The [PersistentRememberMeToken] provided by Spring Security.
     */
    override fun createNewToken(token: PersistentRememberMeToken) {
        val entity = PersistentLogin(
            username = token.username,
            series = token.series,
            token = token.tokenValue,
            lastUsed = token.date.toInstant().atOffset(ZoneOffset.UTC)
        )
        persistentLoginRepository.save(entity)
    }

    /**
     * Updates an existing token with a new value and timestamp.
     *
     * Called each time the user returns with a valid "remember me" cookie.
     *
     * @param series The unique series identifier for the token.
     * @param tokenValue The new token value.
     * @param lastUsed The new last-used date.
     */
    override fun updateToken(series: String, tokenValue: String, lastUsed: java.util.Date) {
        val entity = persistentLoginRepository.findById(series).orElseThrow()
        val updated = entity.copy(
            token = tokenValue,
            lastUsed = lastUsed.toInstant().atOffset(ZoneOffset.UTC)
        )
        persistentLoginRepository.save(updated)
    }

    /**
     * Retrieves a persistent login token for the given series identifier.
     *
     * Called whenever a user presents a "remember-me" cookie in a request.
     *
     * @param seriesId The unique series identifier.
     * @return The [PersistentRememberMeToken] for the series, or `null` if none exist.
     */
    override fun getTokenForSeries(seriesId: String): PersistentRememberMeToken? {
        val entity = persistentLoginRepository.findById(seriesId).orElse(null) ?: return null
        return PersistentRememberMeToken(
            entity.username,
            entity.series,
            entity.token,
            java.util.Date.from(entity.lastUsed.toInstant())
        )
    }

    /**
     * Removes all persistent login tokens associated with the given username.
     *
     * Called when a user logs out.
     *
     * @param username The username whose tokens should be deleted.
     */
    override fun removeUserTokens(username: String) {
        val tokens = persistentLoginRepository.findByUsername(username)
        persistentLoginRepository.deleteAll(tokens)
    }
}
