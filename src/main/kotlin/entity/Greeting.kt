package es.unizar.webeng.hello.entity 

import es.unizar.webeng.hello.enum.Endpoint
import es.unizar.webeng.hello.enum.TimeOfDay
import es.unizar.webeng.hello.enum.timestampToTimeOfDay
import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(name = "GREETINGS")
class Greeting(
    @Enumerated(EnumType.STRING)
    val endpoint: Endpoint,

    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User,

    val name: String,

    val timestamp: OffsetDateTime = OffsetDateTime.now(),

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0  
) {
    val timeOfDay: TimeOfDay
        get() = timestampToTimeOfDay(timestamp)
}
