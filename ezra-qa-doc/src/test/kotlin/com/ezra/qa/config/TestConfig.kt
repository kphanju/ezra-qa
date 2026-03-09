package com.ezra.qa.config

object TestConfig {

    val baseUrl: String = prop("ezra.base.url", "EZRA_BASE_URL")
        ?: "https://stage-api.ezra.com"

    val userAEmail: String = prop("ezra.user.a.email", "EZRA_USER_A_EMAIL")
        ?: error("EZRA_USER_A_EMAIL not configured")

    val userAPassword: String = prop("ezra.user.a.password", "EZRA_USER_A_PASSWORD")
        ?: error("EZRA_USER_A_PASSWORD not configured")

    val userBEmail: String = prop("ezra.user.b.email", "EZRA_USER_B_EMAIL")
        ?: error("EZRA_USER_B_EMAIL not configured")

    val userBPassword: String = prop("ezra.user.b.password", "EZRA_USER_B_PASSWORD")
        ?: error("EZRA_USER_B_PASSWORD not configured")

    val defaultPackageId: String = prop("ezra.package.id", "EZRA_PACKAGE_ID")
        ?: "2b8f4e9a-7c3d-4a1e-9f5b-6d2c8e4a7b3f"

    val defaultCenterId: String = prop("ezra.center.id", "EZRA_CENTER_ID")
        ?: "3abac69a-ea69-4b62-80f1-9b6e84d733af"

    val httpTimeoutSeconds: Long = 30L
    val stripeCardSuccess: String = "pm_card_visa"
    val stripeCardDeclined: String = "pm_card_visa_chargeDeclined"

    val portalUrl: String = prop("ezra.portal.url", "EZRA_PORTAL_URL")
        ?: "https://myezra-staging.ezra.com"

    val headless: Boolean = prop("ezra.headless", "EZRA_HEADLESS")?.lowercase() != "false"

    val uiTimeoutSeconds: Long = 15L

    private fun prop(sysProp: String, envVar: String): String? =
        System.getProperty(sysProp)?.takeIf { it.isNotBlank() }
            ?: System.getenv(envVar)?.takeIf { it.isNotBlank() }
}
