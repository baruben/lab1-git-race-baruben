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

@Component
class RateLimitFilter(
    private val rateLimitService: RateLimitService,
) : OncePerRequestFilter() {

    private fun resolveKey(request: HttpServletRequest, auth: Authentication?): String {
        if (auth != null && auth.isAuthenticated) {
            return "user:${auth.name}"
        }
        return "ip:${request.remoteAddr}"
    }

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
