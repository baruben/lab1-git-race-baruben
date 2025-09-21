package es.unizar.webeng.hello.response


/**
 * Represents the response of the API endpoint that sends a greeting message.
 *
 * @property message The greeting message.
 * @property timestamp The timestamp when the greeting was created, 
 *   formatted as a String (YYYY-MM-DDTHH:MM:SS[.nnnnnnnnn][±HH:MM]).
 */
data class GreetingResponse(
    val message: String,
    val timestamp: String
)


/**
 * Represents the response of the API endpoint that lists a user's history
 * of sent greeting messages.
 *
 * @property message The greeting message.
 * @property endpoint The [Endpoint] through which the greeting was sent formatted as a String.
 * @property timestamp The timestamp when the greeting was created, 
 *   formatted as a String (YYYY-MM-DDTHH:MM:SS[.nnnnnnnnn][±HH:MM]).
 */
data class GreetingHistoryResponse(
    val message: String,
    val endpoint: String,
    val timestamp: String
)