package es.unizar.webeng.hello.service

import es.unizar.webeng.hello.entity.Greeting
import es.unizar.webeng.hello.entity.User
import es.unizar.webeng.hello.enum.Endpoint
import es.unizar.webeng.hello.repository.GreetingRepository
import org.springframework.stereotype.Service


/**
 * Service layer for managing [Greeting] entities.
 *
 * Handles creation and retrieval of greetings, encapsulating
 * the persistence logic provided by [GreetingRepository].
 *
 * @property greetingRepository Repository for CRUD operations on greetings.
 */
@Service
class GreetingService(private val greetingRepository: GreetingRepository) {
    
    /**
     * Creates a new [Greeting] for the specified [user].
     *
     * @param name The name to include in the greeting.
     * @param endpoint The [Endpoint] where the greeting originated (WEB or API).
     * @param user The [User] who sent the greeting.
     * @return The saved [Greeting] entity.
     */
    fun create(name: String, endpoint: Endpoint, user: User): Greeting {
        val greeting = Greeting(
            name = name,
            endpoint = endpoint,
            user = user
        )
        return greetingRepository.save(greeting)
    }

    /**
     * Retrieves all greetings from the database.
     *
     * @return A list of all [Greeting] entities.
     */
    fun listGreetings(): List<Greeting> = greetingRepository.findAll()

    /**
     * Retrieves all greetings for a specific [User], ordered by timestamp descending.
     *
     * @param user The [User] whose greetings should be retrieved.
     * @return A list of [Greeting] entities for the user, newest first.
     */
    fun findAllByUserOrderByTimestampDesc(user: User): List<Greeting> = greetingRepository.findAllByUserOrderByTimestampDesc(user)
}