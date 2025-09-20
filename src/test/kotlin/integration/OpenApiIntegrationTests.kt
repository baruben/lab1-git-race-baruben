package es.unizar.webeng.hello.controller

import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.test.context.TestPropertySource
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.properties")
class OpenApiIntegrationTests {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `should return openapi spec`() {
        mockMvc.perform(get("/v3/api-docs"))
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.openapi").value("3.1.0"))
            .andExpect(jsonPath("$.paths['/api/whoami']").exists())
    }

    @Test
    fun `should return swagger ui`() {
        mockMvc.perform(get("/swagger-ui/index.html"))
            .andExpect(status().isOk)
            .andExpect(content().string(containsString("Swagger UI")))
    }
}
