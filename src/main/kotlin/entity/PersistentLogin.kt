package es.unizar.webeng.hello.entity 

import jakarta.persistence.*
import java.time.OffsetDateTime

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
