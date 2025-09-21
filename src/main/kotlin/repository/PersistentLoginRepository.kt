package es.unizar.webeng.hello.repository

import es.unizar.webeng.hello.entity.PersistentLogin
import org.springframework.data.jpa.repository.JpaRepository


/**
 * Repository interface for managing [PersistentLogin] entities.
 *
 * Provides methods for retrieving persistent login tokens.
 */
interface PersistentLoginRepository : JpaRepository<PersistentLogin, String> {

    /**
     * Finds all persistent login records associated with the given username.
     *
     * @param username The username to search for.
     * @return A list of [PersistentLogin] entries for the user.
     */
    fun findByUsername(username: String): List<PersistentLogin>
}
