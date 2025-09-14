package es.unizar.webeng.hello.data 

import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "GREETINGS")
class Greeting(
    @Enumerated(EnumType.STRING)
    val requestType: RequestType,

    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User,

    val name: String,

    val timestamp: OffsetDateTime = OffsetDateTime.now(),

    @Id 
    val id: UUID = UUID.randomUUID()   
) {
    val timeOfDay: TimeOfDay
        get() = when (timestamp.hour) {
            in 6..11 -> TimeOfDay.MORNING
            in 12..17 -> TimeOfDay.AFTERNOON
            else -> TimeOfDay.NIGHT
        }
}
