package com.ezra.qa.context

import com.ezra.qa.model.response.EncounterResponse
import com.ezra.qa.model.response.PendingPaymentResponse
import okhttp3.Response
import com.ezra.qa.driver.DriverFactory
import com.ezra.qa.pages.*

class ScenarioContext {

    var userAToken: String = ""
    var userAMemberId: String = ""
    var userBToken: String = ""
    var userBMemberId: String = ""

    var userAEncounterId: String = ""
    var userAEncounterResponse: EncounterResponse? = null
    var userAPaymentResponse: PendingPaymentResponse? = null

    var userBEncounterId: String = ""
    var userBEncounterResponse: EncounterResponse? = null

    var lastHttpStatusCode: Int = 0
    var lastResponseBody: String = ""

    fun capture(response: Response, body: String) {
        lastHttpStatusCode = response.code
        lastResponseBody = body
    }

    fun requireUserAToken(): String =
        userAToken.ifBlank { error("User A not authenticated") }

    fun requireUserBToken(): String =
        userBToken.ifBlank { error("User B not authenticated") }

    fun requireUserAEncounterId(): String =
        userAEncounterId.ifBlank { error("User A encounterId not set") }

    fun requireUserBEncounterId(): String =
        userBEncounterId.ifBlank { error("User B encounterId not set") }

    // ── UI state ─────────────────────────────────────────────────────────────
    val driverFactory: DriverFactory by lazy { DriverFactory() }

    val loginPage by lazy { LoginPage(driverFactory) }
    val scanSelectionPage by lazy { ScanSelectionPage(driverFactory) }
    val schedulePage by lazy { SchedulePage(driverFactory) }
    val paymentPage by lazy { PaymentPage(driverFactory) }

    var capturedOrderTotal: String = ""

    fun quitDriver() = runCatching { driverFactory.quit() }
}
