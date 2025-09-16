package es.unizar.webeng.hello.enum

import java.time.OffsetDateTime

enum class RequestType { WEB, API }

enum class TimeOfDay(val message: String) { 
    MORNING("Good Morning"), 
    AFTERNOON("Good Afternoon"), 
    NIGHT("Good Night") 
}

fun timestampToTimeOfDay(timestamp: OffsetDateTime): TimeOfDay {
    return when (timestamp.hour) {
        in 6..11 -> TimeOfDay.MORNING
        in 12..17 -> TimeOfDay.AFTERNOON
        else -> TimeOfDay.NIGHT
    }
}