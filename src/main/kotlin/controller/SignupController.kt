package es.unizar.webeng.hello.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class SignupController {
  
    @GetMapping("/signup")
    fun signup(): String {
        return "signup"
    }
}
