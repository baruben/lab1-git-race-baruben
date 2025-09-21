package es.unizar.webeng.hello.filter

import io.github.bucket4j.ConsumptionProbe
import es.unizar.webeng.hello.enum.Role
import es.unizar.webeng.hello.service.RateLimitService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.concurrent.TimeUnit
import org.springframework.security.core.Authentication


/**
 * A servlet filter that applies rate limiting to API requests, using
 * [RateLimitService] and Bucket4j to limit the number of requests a user
 * or IP can make within a given time window.
 *
 * ---
 *
 * ## Key resolution:
 * - If the user is authenticated, the rate limit key is `"user:{username}"`.
 * - Otherwise, the key falls back to `"ip:{remoteAddr}"`.
 *
 * ---
 *
 * ## Behavior:
 * - Applies only to requests with URIs starting with `/api/`.
 * - Retrieves a [io.github.bucket4j.Bucket] from [RateLimitService] for the key.
 * - Attempts to consume 1 token per request:
 *   - **Success** → request proceeds, response includes header:
 *     - `X-RateLimit-Remaining` with the number of tokens left.
 *   - **Failure** (no tokens left) → request blocked with:
 *     - HTTP **429 Too Many Requests**
 *     - `Retry-After` header (in seconds until refill)
 *     - JSON error body: `{"error":"Too Many Requests","retryAfter":<seconds>}`
 *
 * ---
 *
 * ## Example flow:
 * ```
 * Client → /api/resource
 *   ↳ [RateLimitFilter] checks bucket
 *       ↳ If tokens available → continue to controller
 *       ↳ If empty → respond 429 + Retry-After
 * ```
 *
 * @property rateLimitService Service that provides rate limit buckets per key.
 */
@Component
class RateLimitFilter(
    private val rateLimitService: RateLimitService,
) : OncePerRequestFilter() {

    /**
     * Resolves the rate limit key for the current request.
     *
     * @param request The incoming HTTP request.
     * @param auth The current authentication object (nullable).
     * @return A unique key based on user (if authenticated) or client IP address.
     */
    private fun resolveKey(request: HttpServletRequest, auth: Authentication?): String {
        if (auth != null && auth.isAuthenticated) {
            return "user:${auth.name}"
        }
        return "ip:${request.remoteAddr}"
    }

    /**
     * Applies rate limiting before passing the request down the filter chain.
     *
     * @param request The HTTP request.
     * @param response The HTTP response.
     * @param filterChain The servlet filter chain.
     */
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (request.requestURI.startsWith("/api/")) {
            val auth = SecurityContextHolder.getContext().authentication
            val key = resolveKey(request, auth)
            val bucket = rateLimitService.resolveBucket(key)

            val probe: ConsumptionProbe = bucket.tryConsumeAndReturnRemaining(1)
            if (!probe.isConsumed) {
                val retryAfter = TimeUnit.NANOSECONDS.toSeconds(probe.nanosToWaitForRefill)

                response.status = 429
                response.setHeader("Retry-After", retryAfter.toString())
                response.contentType = MediaType.APPLICATION_JSON_VALUE
                response.writer.write("""{"error":"Too Many Requests","retryAfter":$retryAfter}""")
                return
            } else {
                response.setHeader("X-RateLimit-Remaining", probe.remainingTokens.toString())
            }
        }

        filterChain.doFilter(request, response)
    }
}
