package es.unizar.webeng.hello.entity 

import jakarta.persistence.*
import java.time.OffsetDateTime


/**
 * Represents a persistent login token for a user.
 *
 * Used to implement "remember me" functionality in Spring Security authentication system.
 *
 * @property series A unique identifier for the persistent login. Serves as the primary key.
 * @property username The username of the user associated with this login token.
 * @property token The actual token value used for authentication.
 * @property lastUsed The timestamp of the last time this token was used. Defaults to the current time.
 */
@Entity
@Table(name = "PERSISTENT_LOGINS")
data class PersistentLogin(
    @Id
    val series: String = "",

    @Column(nullable = false)
    val username: String = "",

    @Column(nullable = false)
    val token: String = "",

    @Column(name = "last_used", nullable = false)
    val lastUsed: OffsetDateTime = OffsetDateTime.now()
)
