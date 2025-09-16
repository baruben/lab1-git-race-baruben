package es.unizar.webeng.hello.service

import es.unizar.webeng.hello.entity.Greeting
import es.unizar.webeng.hello.entity.User
import es.unizar.webeng.hello.enum.RequestType
import es.unizar.webeng.hello.repository.GreetingRepository
import org.springframework.stereotype.Service

@Service
class GreetingService(private val db: GreetingRepository) {
    fun create(name: String, requestType: RequestType, user: User): Greeting {
        val greeting = Greeting(
            name = name,
            requestType = requestType,
            user = user
        )
        return db.save(greeting)
    }

    fun listGreetings(): List<Greeting> = db.findAll()
}