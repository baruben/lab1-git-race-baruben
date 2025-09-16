package es.unizar.webeng.hello.repository

import es.unizar.webeng.hello.entity.PersistentLogin
import org.springframework.data.jpa.repository.JpaRepository

interface PersistentLoginRepository : JpaRepository<PersistentLogin, String> {
    fun findByUsername(username: String): List<PersistentLogin>
}
