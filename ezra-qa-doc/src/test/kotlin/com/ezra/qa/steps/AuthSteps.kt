package com.ezra.qa.steps

import com.ezra.qa.client.EzraApiClient
import com.ezra.qa.config.TestConfig
import com.ezra.qa.context.ScenarioContext
import io.cucumber.java8.En
import org.assertj.core.api.Assertions.assertThat

class AuthSteps(
    private val context: ScenarioContext,
    private val client: EzraApiClient
) : En {
    init {
        Given("User A is authenticated with valid credentials") {
            val (response, body) = client.authenticate(TestConfig.userAEmail, TestConfig.userAPassword)
            context.capture(response, body)
            assertThat(response.code).`as`("User A auth").isEqualTo(200)
            val auth = client.parseAuth(body)
            assertThat(auth.access_token).isNotBlank()
            context.userAToken = auth.access_token
            context.userAMemberId = extractSub(auth.access_token)
        }

        Given("User B is authenticated with valid credentials") {
            val (response, body) = client.authenticate(TestConfig.userBEmail, TestConfig.userBPassword)
            context.capture(response, body)
            assertThat(response.code).`as`("User B auth").isEqualTo(200)
            val auth = client.parseAuth(body)
            context.userBToken = auth.access_token
            context.userBMemberId = extractSub(auth.access_token)
        }
    }

    private fun extractSub(jwt: String): String = try {
        val payload = jwt.split(".")[1]
        val decoded = String(java.util.Base64.getUrlDecoder().decode(pad(payload)))
        Regex(""""sub"\s*:\s*"([^"]+)"""").find(decoded)?.groupValues?.get(1) ?: ""
    } catch (e: Exception) { "" }

    private fun pad(s: String) = s + "=".repeat((4 - s.length % 4) % 4)
}
