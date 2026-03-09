package com.ezra.qa.steps.ui

import com.ezra.qa.config.TestConfig
import com.ezra.qa.context.ScenarioContext
import io.cucumber.java8.En
import org.assertj.core.api.Assertions.assertThat

/**
 * UI step definitions for the three-step booking flow.
 *
 * Each step delegates to a page object method — no WebDriver or XPath
 * calls live in this class. All DOM interaction is encapsulated in pages/.
 *
 * PicoContainer injects [context]. Page objects are accessed via lazy
 * properties on context so the browser only launches when a UI step runs.
 * API-only scenarios never open a browser.
 */
class BookingUiSteps(private val context: ScenarioContext) : En {

    init {

        // ─────────────────────────────────────────────────────────────────────
        // Login
        // ─────────────────────────────────────────────────────────────────────

        Given("the member portal is open") {
            context.loginPage.navigate()
        }

        Given("User A is logged in to the portal") {
            context.loginPage.loginAs(
                email    = TestConfig.userAEmail,
                password = TestConfig.userAPassword
            )
            assertThat(context.loginPage.isLoggedIn())
                .`as`("User A should be logged in after providing valid credentials")
                .isTrue()
        }

        Given("User B is logged in to the portal") {
            context.loginPage.loginAs(
                email    = TestConfig.userBEmail,
                password = TestConfig.userBPassword
            )
            assertThat(context.loginPage.isLoggedIn())
                .`as`("User B should be logged in after providing valid credentials")
                .isTrue()
        }

        When("a member logs in with email {string} and password {string}") {
                email: String, password: String ->
            context.loginPage.loginAs(email, password)
        }

        Then("the member should be redirected to the dashboard") {
            context.loginPage.waitUntilLoggedIn()
            assertThat(context.loginPage.currentUrl())
                .`as`("URL should contain /dashboard after login")
                .contains("dashboard")
        }

        Then("a login error message is displayed") {
            assertThat(context.loginPage.isLoginErrorDisplayed())
                .`as`("Login error should be visible for invalid credentials")
                .isTrue()
        }

        Then("the login error message text is {string}") { expectedText: String ->
            assertThat(context.loginPage.getLoginErrorText())
                .`as`("Login error text")
                .containsIgnoringCase(expectedText)
        }

        // ─────────────────────────────────────────────────────────────────────
        // Step 1 — Scan Selection
        // ─────────────────────────────────────────────────────────────────────

        Given("the member navigates to the scan selection page") {
            context.scanSelectionPage.navigate()
            context.scanSelectionPage.waitUntilLoaded()
        }

        When("the member selects the {string} plan") { planName: String ->
            context.scanSelectionPage.selectPlan(planName)
        }

        When("the member clicks Continue on the scan selection page") {
            context.scanSelectionPage.clickContinue()
        }

        When("the member clicks Cancel on the scan selection page") {
            context.scanSelectionPage.clickCancel()
        }

        When("the member clicks the info icon on the {string} plan") { planName: String ->
            context.scanSelectionPage.clickPlanInfoIcon(planName)
        }

        Then("the {string} plan should be highlighted as selected") { planName: String ->
            assertThat(context.scanSelectionPage.isPlanSelected(planName))
                .`as`("Plan '$planName' should show selected state after clicking")
                .isTrue()
        }

        Then("the {string} plan price should be {string}") {
                planName: String, expectedPrice: String ->
            val actual = context.scanSelectionPage.getPlanPriceAmount(planName)
            assertThat(actual)
                .`as`("Price displayed for plan '$planName'")
                .isEqualTo(expectedPrice)
        }

        Then("all four scan plans are visible") {
            val plans = context.scanSelectionPage.getVisiblePlanNames()
            assertThat(plans)
                .`as`("All four plan cards should be visible on the page")
                .hasSizeGreaterThanOrEqualTo(4)
        }

        Then("the Continue button on scan selection is disabled") {
            assertThat(context.scanSelectionPage.isContinueButtonEnabled())
                .`as`("Continue should be disabled before any plan is selected")
                .isFalse()
        }

        Then("the Continue button on scan selection is enabled") {
            assertThat(context.scanSelectionPage.isContinueButtonEnabled())
                .`as`("Continue should be enabled after a plan is selected")
                .isTrue()
        }

        Then("the {string} plan shows an info icon") { planName: String ->
            assertThat(context.scanSelectionPage.isPlanInfoIconVisible(planName))
                .`as`("Info icon should be visible on '$planName' card")
                .isTrue()
        }

        Then("the member is on the scan selection page") {
            assertThat(context.scanSelectionPage.isOnScanSelectionPage())
                .`as`("Member should still be on the scan selection page")
                .isTrue()
        }

        // ─────────────────────────────────────────────────────────────────────
        // Step 2 — Schedule
        // ─────────────────────────────────────────────────────────────────────

        Given("the member is on the schedule page") {
            context.schedulePage.waitUntilLoaded()
            assertThat(context.schedulePage.isOnSchedulePage())
                .`as`("Should be on the Schedule your scan step")
                .isTrue()
        }

        When("the member searches for location {string}") { query: String ->
            context.schedulePage.searchLocation(query)
        }

        When("the member selects center {string}") { centerName: String ->
            context.schedulePage.selectCenter(centerName)
        }

        When("the member selects the time slot {string}") { time: String ->
            context.schedulePage.selectTimeSlot(time)
        }

        When("the member selects the first available time slot") {
            context.schedulePage.selectFirstAvailableSlot()
        }

        When("the member clicks Continue on the schedule page") {
            context.schedulePage.clickContinue()
        }

        When("the member clicks Back on the schedule page") {
            context.schedulePage.clickBack()
        }

        Then("the time slot {string} should be selected") { time: String ->
            assertThat(context.schedulePage.isTimeSlotSelected(time))
                .`as`("Time slot '$time' should show a selected state")
                .isTrue()
        }

        Then("a time slot is selected") {
            assertThat(context.schedulePage.hasAnySlotSelected())
                .`as`("At least one time slot should be selected")
                .isTrue()
        }

        Then("available time slots are displayed") {
            val slots = context.schedulePage.getAvailableSlotTimes()
            assertThat(slots)
                .`as`("At least one available time slot should be visible")
                .isNotEmpty()
        }

        Then("a no-availability message is shown") {
            assertThat(context.schedulePage.hasNoAvailabilityMessage())
                .`as`("No-availability message should be visible")
                .isTrue()
        }

        Then("the Continue button on the schedule page is enabled") {
            assertThat(context.schedulePage.isContinueEnabled())
                .`as`("Continue should be enabled after selecting a slot")
                .isTrue()
        }

        Then("the member is back on the scan selection page") {
            assertThat(context.scanSelectionPage.isOnScanSelectionPage())
                .`as`("Back navigation should return to scan selection step")
                .isTrue()
        }

        Then("the member is back on the schedule page") {
            assertThat(context.schedulePage.isOnSchedulePage())
                .`as`("Back navigation from payment should return to schedule page")
                .isTrue()
        }

        // ─────────────────────────────────────────────────────────────────────
        // Step 3 — Payment
        // ─────────────────────────────────────────────────────────────────────

        Given("the member is on the payment page") {
            context.paymentPage.waitUntilLoaded()
            assertThat(context.paymentPage.isOnPaymentPage())
                .`as`("Should be on the Reserve your appointment page")
                .isTrue()
        }

        When("the member enters promo code {string}") { code: String ->
            context.paymentPage.enterPromoCode(code)
        }

        When("the member clicks Apply Code") {
            context.paymentPage.clickApplyCode()
        }

        When("the member applies promo code {string}") { code: String ->
            context.paymentPage.applyPromoCode(code)
        }

        When("the member selects Bank payment") {
            context.paymentPage.selectBankPayment()
        }

        When("the member selects Link payment") {
            context.paymentPage.selectLinkPayment()
        }

        When("the member selects the {string} bank tile") { label: String ->
            context.paymentPage.selectBankTile(label)
        }

        When("the member clicks Use this card") {
            context.paymentPage.clickUseThisCard()
        }

        When("the member clicks Continue on the payment page") {
            context.paymentPage.clickContinue()
        }

        When("the member clicks Back on the payment page") {
            context.paymentPage.clickBack()
            context.schedulePage.waitUntilLoaded()
        }

        // ── Order summary assertions ──────────────────────────────────────────

        Then("the order total should be {string}") { expectedTotal: String ->
            val actual = context.paymentPage.getOrderTotal()
            assertThat(actual)
                .`as`("Order total in the sidebar")
                .isEqualTo(expectedTotal)
        }

        Then("the order total should still be {string}") { expectedTotal: String ->
            val actual = context.paymentPage.getOrderTotal()
            assertThat(actual)
                .`as`("Order total should be unchanged after an invalid promo code")
                .isEqualTo(expectedTotal)
        }

        Then("the member captures the current order total") {
            context.capturedOrderTotal = context.paymentPage.getOrderTotal()
            println("[UI] Captured order total: ${context.capturedOrderTotal}")
        }

        Then("the order total is lower than the captured total") {
            val original = context.capturedOrderTotal
                .replace("$", "")
                .replace(",", "")
                .trim()
                .toDoubleOrNull()
                ?: error(
                    "Captured total '${context.capturedOrderTotal}' " +
                        "could not be parsed as a number"
                )

            val currentText = context.paymentPage.getOrderTotal()
            val current = currentText
                .replace("$", "")
                .replace(",", "")
                .trim()
                .toDoubleOrNull()
                ?: error(
                    "Current total '$currentText' " +
                        "could not be parsed as a number"
                )

            assertThat(current)
                .`as`("Total after promo should be less than original \$$original")
                .isLessThan(original)
        }

        // ── Promo code assertions ─────────────────────────────────────────────

        Then("an Invalid Promo Code error is displayed") {
            assertThat(context.paymentPage.isInvalidPromoErrorDisplayed())
                .`as`("'Invalid Promo Code' error message should be visible")
                .isTrue()
        }

        Then("the promo error message reads {string}") { expectedText: String ->
            assertThat(context.paymentPage.getInvalidPromoErrorText())
                .`as`("Promo code error text")
                .containsIgnoringCase(expectedText)
        }

        Then("no promo error is displayed") {
            assertThat(context.paymentPage.isInvalidPromoErrorDisplayed())
                .`as`("No promo error should be visible before applying a code")
                .isFalse()
        }

        Then("the promo code is applied successfully") {
            assertThat(context.paymentPage.isPromoAppliedSuccessfully())
                .`as`("Promo applied success indicator should be visible")
                .isTrue()
        }

        Then("the promo code input is visible") {
            assertThat(context.paymentPage.isPromoCodeInputVisible())
                .`as`("Promo code input field should be visible on payment page")
                .isTrue()
        }

        // ── Bank / card assertions ────────────────────────────────────────────

        Then("the bank cashback banner is displayed") {
            assertThat(context.paymentPage.isBankCashbackBannerVisible())
                .`as`("'\$5 back when you pay by bank' banner should be visible")
                .isTrue()
        }

        Then("the bank cashback banner mentions {string}") { text: String ->
            assertThat(context.paymentPage.getBankCashbackBannerText())
                .`as`("Bank cashback banner text")
                .containsIgnoringCase(text)
        }

        Then("the {string} bank tile is visible") { label: String ->
            assertThat(context.paymentPage.isBankTileVisible(label))
                .`as`("Bank tile '$label' should be visible")
                .isTrue()
        }

        Then("all bank payment tiles are visible") {
            listOf("Success", "Blocked", "Disputed").forEach { tile ->
                assertThat(context.paymentPage.isBankTileVisible(tile))
                    .`as`("Bank tile '$tile' should be visible")
                    .isTrue()
            }
        }

        Then("the Visa card is displayed in the payment section") {
            assertThat(context.paymentPage.isVisaCardRowVisible())
                .`as`("Visa credit card row should be visible under Stripe Link")
                .isTrue()
        }

        // ── Confirmation ──────────────────────────────────────────────────────

        Then("the booking confirmation page is displayed") {
            context.confirmationPage.waitUntilLoaded()
            assertThat(context.confirmationPage.isConfirmationDisplayed())
                .`as`("Confirmation page heading should be visible after payment")
                .isTrue()
        }
    }
}
