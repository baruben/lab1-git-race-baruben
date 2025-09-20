package es.unizar.webeng.hello.enum

import java.time.OffsetDateTime


/**
 * Represents the possible endpoints through which a greeting can be sent.
 */
enum class Endpoint { 

    /** Web-based endpoint */
    WEB, 

    /** API-based endpoint */
    API }


/**
 * Represents different times of the day for greetings.
 *
 * @property message The default greeting message associated with this time of day.
 */
enum class TimeOfDay(val message: String) { 

    /** Morning time (6:00–11:59) */
    MORNING("Good Morning"), 

    /** Afternoon time (12:00–17:59) */
    AFTERNOON("Good Afternoon"), 

    /** Night time (18:00–5:59) */
    NIGHT("Good Night") 
}


/**
 * Converts a timestamp to the corresponding [TimeOfDay].
 *
 * @param timestamp The OffsetDateTime to evaluate.
 * @return The [TimeOfDay] that corresponds to the hour of the timestamp.
 */
fun timestampToTimeOfDay(timestamp: OffsetDateTime): TimeOfDay {
    return when (timestamp.hour) {
        in 6..11 -> TimeOfDay.MORNING
        in 12..17 -> TimeOfDay.AFTERNOON
        else -> TimeOfDay.NIGHT
    }
}