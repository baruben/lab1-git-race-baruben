package es.unizar.webeng.hello.data 

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "USERS")
class User(
    val username: String,

    val mail: String,

    val password: String,

    @Enumerated(EnumType.STRING)
    val userType: UserType,
    
    @Id 
    val id: UUID = UUID.randomUUID()
)
