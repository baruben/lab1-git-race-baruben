package es.unizar.webeng.hello.response

data class GreetingResponse(
    val message: String,
    val timestamp: String
)

data class GreetingHistoryResponse(
    val message: String,
    val from: String,
    val timestamp: String
)