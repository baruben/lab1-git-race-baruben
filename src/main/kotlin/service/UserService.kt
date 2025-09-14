package es.unizar.webeng.hello.service

import es.unizar.webeng.hello.data.User
import es.unizar.webeng.hello.data.UserType
import es.unizar.webeng.hello.repository.UserRepository
import org.springframework.stereotype.Service
import jakarta.annotation.PostConstruct
import java.util.UUID

@Service
class UserService(private val db: UserRepository) {
    lateinit var guest: User

    @PostConstruct
    fun initInvitado() {
        guest = db.findByUserType(UserType.GUEST) ?: db.save(
            User(username = "", mail = "", password = "", 
                userType = UserType.GUEST)
            )
    }
}