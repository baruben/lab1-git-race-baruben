package es.unizar.webeng.hello.configuration

import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


/**
 * Configuration for OpenAPI/Swagger documentation.
 *
 * Declares the API's security scheme and general metadata.
 *
 * ---
 *
 * ## Security
 * - Defines a cookie-based authentication scheme (`cookieAuth`) using `JSESSIONID`.
 * - This allows Swagger UI to handle session-based authentication
 *   by sending the session cookie with API requests.
 */
@Configuration
@SecurityScheme(
    name = "cookieAuth",
    type = SecuritySchemeType.APIKEY,
    `in` = SecuritySchemeIn.COOKIE,
    paramName = "JSESSIONID"
)
class OpenApiConfig {

    /**
     * Builds the custom OpenAPI specification with application metadata.
     *
     * @return [OpenAPI] instance with title, version, and description.
     */
    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Modern Web App API")
                    .version("v1.0")
                    .description("""
                    OpenAPI Documentation of the Modern Web App API
                    This API enforces **rate limits**:  
                    - 50 requests per minute per user.  
                    - Exceeding the limit returns HTTP 429 Too Many Requests.  
                    - Responses include headers: `X-RateLimit-Limit`, `X-RateLimit-Remaining`, and `Retry-After`.
                    """.trimIndent())
            )
    }
}
