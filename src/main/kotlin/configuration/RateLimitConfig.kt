package es.unizar.webeng.hello.configuration

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.Bucket4j
import io.github.bucket4j.Refill
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.boot.web.servlet.FilterRegistrationBean
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

@Configuration
class RateLimitConfig {

    private val cache = ConcurrentHashMap<String, Bucket>()

    private fun resolveBucket(ip: String): Bucket {
        return cache.computeIfAbsent(ip) {
            val limit: Bandwidth = Bandwidth.classic(
                100, 
                Refill.greedy(100, Duration.ofMinutes(1)) 
            )
            Bucket4j.builder().addLimit(limit).build()
        }
    }

    @Bean
    fun rateLimiterFilter(): FilterRegistrationBean<Filter> {
        val registrationBean = FilterRegistrationBean<Filter>()

        val filter = Filter { request: ServletRequest, response: ServletResponse, chain: FilterChain ->
            val httpRequest = request as HttpServletRequest
            val httpResponse = response as HttpServletResponse

            val path = httpRequest.requestURI

            if (path.startsWith("/api/")) {
                val ip = httpRequest.remoteAddr
                val bucket = resolveBucket(ip)

                if (bucket.tryConsume(1)) {
                    chain.doFilter(request, response)
                } else {
                    httpResponse.status = 429
                    httpResponse.setHeader("Retry-After", "60")
                    httpResponse.writer.write("Too many requests, please try again later.")
                }
            } else {
                chain.doFilter(request, response)
            }
        }

        registrationBean.filter = filter
        registrationBean.order = 1 
        return registrationBean
    }
}
