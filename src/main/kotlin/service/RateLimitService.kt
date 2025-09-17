package es.unizar.webeng.hello.service

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.Refill
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Value
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap


@Service
class RateLimitService(
    @Value("\${ratelimit.capacity}") private val capacity: Long
) {
    private val buckets: ConcurrentHashMap<String, Bucket> = ConcurrentHashMap()

    private fun newBucket(): Bucket {
        val refill = Refill.intervally(capacity, Duration.ofMinutes(1))
        val limit = Bandwidth.classic(capacity, refill)
        return Bucket.builder()
            .addLimit(limit)
            .build()
    }

    fun resolveBucket(key: String): Bucket {
        return buckets.computeIfAbsent(key) { newBucket() }
    }
}
