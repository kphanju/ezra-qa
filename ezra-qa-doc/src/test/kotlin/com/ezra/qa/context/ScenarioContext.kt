package com.ezra.qa.context

import com.ezra.qa.driver.DriverFactory
import com.ezra.qa.model.response.EncounterResponse
import com.ezra.qa.model.response.PendingPaymentResponse
import com.ezra.qa.pages.ConfirmationPage
import com.ezra.qa.pages.LoginPage
import com.ezra.qa.pages.PaymentPage
import com.ezra.qa.pages.ScanSelectionPage
import com.ezra.qa.pages.SchedulePage
import okhttp3.Response

/**
 * Shared mutable state injected by PicoContainer into every step definition
 * class within a single scenario. One fresh instance per scenario —
 * automatically reset between scenarios by PicoContainer.
 *
 * Naming convention:
 *   userA* — the primary authenticated member (the "owner")
 *   userB* — the second member used in isolation / IDOR tests
 *
 * UI state is lazy — the browser only launches if a UI step actually
 * accesses driverFactory. API-only scenarios never open Chrome.
 */
class ScenarioContext {

    // ── API auth tokens ───────────────────────────────────────────────────────

    var userAToken: String = ""
    var userAMemberId: String = ""

    var userBToken: String = ""
    var userBMemberId: String = ""

    // ── Booking flow state (User A) ───────────────────────────────────────────

    var userAEncounterId: String = ""
    var userAEncounterResponse: EncounterResponse? = null
    var userAPaymentResponse: PendingPaymentResponse? = null

    // ── Booking flow state (User B) ───────────────────────────────────────────

    var userBEncounterId: String = ""
    var userBEncounterResponse: EncounterResponse? = null

    // ── Raw HTTP capture ──────────────────────────────────────────────────────

    /** Status code and body of the most recent HTTP response. */
    var lastHttpStatusCode: Int = 0
    var lastResponseBody: String = ""

    // ── UI / Selenium state ───────────────────────────────────────────────────

    /**
     * DriverFactory is lazy — Chrome only launches when a UI step
     * accesses it for the first time in a scenario.
     */
    val driverFactory: DriverFactory by lazy { DriverFactory() }

    // Page objects — all share the same DriverFactory instance
    val loginPage:          LoginPage          by lazy { LoginPage(driverFactory) }
    val scanSelectionPage:  ScanSelectionPage  by lazy { ScanSelectionPage(driverFactory) }
    val schedulePage:       SchedulePage       by lazy { SchedulePage(driverFactory) }
    val paymentPage:        PaymentPage        by lazy { PaymentPage(driverFactory) }
    val confirmationPage:   ConfirmationPage   by lazy { ConfirmationPage(driverFactory) }

    // Captured UI values used for cross-step assertions
    var capturedOrderTotal: String = ""
    var capturedPlanName:   String = ""

    // ── HTTP helpers ──────────────────────────────────────────────────────────

    /**
     * Reads the response body eagerly and stores both the status code
     * and body string so step definitions can assert on either.
     */
    fun capture(response: Response, body: String) {
        lastHttpStatusCode = response.code
        lastResponseBody = body
    }

    // ── Guard helpers ─────────────────────────────────────────────────────────

    fun requireUserAToken(): String =
        userAToken.ifBlank {
            error("User A is not authenticated — add 'Given User A is authenticated' to the scenario")
        }

    fun requireUserBToken(): String =
        userBToken.ifBlank {
            error("User B is not authenticated — add 'Given User B is authenticated' to the scenario")
        }

    fun requireUserAEncounterId(): String =
        userAEncounterId.ifBlank {
            error("User A encounterId is not set — run the encounter creation step first")
        }

    fun requireUserBEncounterId(): String =
        userBEncounterId.ifBlank {
            error("User B encounterId is not set — run the encounter creation step first")
        }

    // ── Browser teardown ─────────────────────────────────────────────────────

    /**
     * Closes the browser. Called from the After{} hook in Hooks.kt.
     * Safe to call even if the driver was never initialised —
     * runCatching swallows the UninitializedPropertyAccessException.
     */
    fun quitDriver() = runCatching { driverFactory.quit() }
}
