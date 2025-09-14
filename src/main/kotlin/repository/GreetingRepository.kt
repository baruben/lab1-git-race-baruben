package es.unizar.webeng.hello.repository

import es.unizar.webeng.hello.data.Greeting
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface GreetingRepository : JpaRepository<Greeting, UUID>
