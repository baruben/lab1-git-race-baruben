package es.unizar.webeng.hello.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import java.time.LocalTime

fun greet(hour: Int): String {
    val greeting = when (hour) {
        in 6..12 -> "Good Morning"
        in 13..20 -> "Good Afternoon"
        else -> "Good Night"
    }
    return greeting
}

@Controller
class HelloController(
    @param:Value("\${app.message:Hello World}") 
    private val message: String
) {
    
    @GetMapping("/")
    fun welcome(
        model: Model,
        @RequestParam(defaultValue = "") name: String
    ): String {
        val hour = LocalTime.now().hour
        val greeting = if (name.isNotBlank()) "${greet(hour)}, $name!" else message
        model.addAttribute("message", greeting)
        model.addAttribute("name", name)
        return "welcome"
    }
}

@RestController
class HelloApiController {
    
    @GetMapping("/api/hello", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun helloApi(@RequestParam(defaultValue = "World") name: String): Map<String, String> {
        val hour = LocalTime.now().hour
        return mapOf(
            "message" to "${greet(hour)}, $name!",
            "timestamp" to LocalTime.now().toString()
            // "timestamp" to java.time.Instant.now().toString()
        )
    }
}
