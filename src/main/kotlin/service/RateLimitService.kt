package es.unizar.webeng.hello.service

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.Refill
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Value
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap


/**
 * Service layer for managing rate-limiting buckets per user or IP, 
 * using Bucket4j to implement a token-bucket. Each key (user or IP) gets 
 * its own bucket with a capacity defined in application properties.
 *
 * @property capacity Maximum number of requests allowed per key per minute.
 */
@Service
class RateLimitService(
    @param:Value("\${ratelimit.capacity}") private val capacity: Long
) {

    /**
     * Map storing buckets per key (user or IP).
     */
    private val buckets: ConcurrentHashMap<String, Bucket> = ConcurrentHashMap()

    /**
     * Creates a new token bucket with a fixed capacity and refill rate.
     *
     * - Capacity: [capacity] tokens.
     * - Refill: [capacity] tokens every 1 minute.
     *
     * @return A new [Bucket] instance.
     */
    private fun newBucket(): Bucket {
        val refill = Refill.intervally(capacity, Duration.ofMinutes(1))
        val limit = Bandwidth.classic(capacity, refill)
        return Bucket.builder()
            .addLimit(limit)
            .build()
    }

    /**
     * Resolves a bucket for the given key.
     *
     * If a bucket already exists for the key, it is returned.
     * Otherwise, a new bucket is created and stored.
     *
     * @param key Unique identifier for rate-limiting.
     * @return The [Bucket] associated with the key or a new one if none exists.
     */
    fun resolveBucket(key: String): Bucket {
        return buckets.computeIfAbsent(key) { newBucket() }
    }
}
