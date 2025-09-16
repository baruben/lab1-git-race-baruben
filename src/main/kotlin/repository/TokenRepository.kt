package es.unizar.webeng.hello.repository

import es.unizar.webeng.hello.entity.PersistentLogin
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.ZoneId

@Component
class TokenRepository(
    private val persistentLoginRepository: PersistentLoginRepository
) : PersistentTokenRepository {

    override fun createNewToken(token: PersistentRememberMeToken) {
        val entity = PersistentLogin(
            username = token.username,
            series = token.series,
            token = token.tokenValue,
            lastUsed = token.date.toInstant().atOffset(ZoneOffset.UTC)
        )
        persistentLoginRepository.save(entity)
    }

    override fun updateToken(series: String, tokenValue: String, lastUsed: java.util.Date) {
        val entity = persistentLoginRepository.findById(series).orElseThrow()
        val updated = entity.copy(
            token = tokenValue,
            lastUsed = lastUsed.toInstant().atOffset(ZoneOffset.UTC)
        )
        persistentLoginRepository.save(updated)
    }

    override fun getTokenForSeries(seriesId: String): PersistentRememberMeToken? {
        val entity = persistentLoginRepository.findById(seriesId).orElse(null) ?: return null
        return PersistentRememberMeToken(
            entity.username,
            entity.series,
            entity.token,
            java.util.Date.from(entity.lastUsed.toInstant())
        )
    }

    override fun removeUserTokens(username: String) {
        val tokens = persistentLoginRepository.findByUsername(username)
        persistentLoginRepository.deleteAll(tokens)
    }
}
