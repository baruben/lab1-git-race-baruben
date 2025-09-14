package es.unizar.webeng.hello.data 

enum class UserType { GUEST, USER, ADMIN }

enum class RequestType { WEB, API }

enum class TimeOfDay(val message: String) { 
    MORNING("Good Morning"), 
    AFTERNOON("Good Afternoon"), 
    NIGHT("Good Night") 
}
