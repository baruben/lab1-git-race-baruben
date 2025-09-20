package es.unizar.webeng.hello.unit.service

import es.unizar.webeng.hello.service.RateLimitService
import io.github.bucket4j.Bucket
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RateLimitServiceTests {

    private lateinit var rateLimitService: RateLimitService

    @BeforeEach
    fun setup() {
        rateLimitService = RateLimitService(5)
    }

    @Test
    fun `resolveBucket should create new bucket for unknown key`() {
        val bucket: Bucket = rateLimitService.resolveBucket("test")

        assertThat(bucket).isNotNull
    }

    @Test
    fun `resolveBucket should return same bucket for same key`() {
        val bucket1 = rateLimitService.resolveBucket("test")
        val bucket2 = rateLimitService.resolveBucket("test")

        assertThat(bucket2).isSameAs(bucket1)
    }

    @Test
    fun `resolveBucket should create different buckets for different keys`() {
        val bucket1 = rateLimitService.resolveBucket("test")
        val bucket2 = rateLimitService.resolveBucket("other")

        assertThat(bucket2).isNotSameAs(bucket1)
    }
}
