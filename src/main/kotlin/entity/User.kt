package es.unizar.webeng.hello.entity

import es.unizar.webeng.hello.enum.Role
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "USERS")
class User(
    @Column(unique = true, nullable = false)
    val username: String = "",

    @Column(nullable = false)
    val password: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: Role = Role.USER,
    
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
)
