package com.ezra.qa.pages


import com.ezra.qa.driver.DriverFactory

/**
 * Page Object for "Reserve your appointment" — Step 3 of the booking flow.
 * URL: /book-scan/reserve-appointment
 *
 * Screenshot references:
 *   - payment_with_promo.pdf: Visa Credit card via Stripe Link, promo code field, $999 total
 *   - payment_bank.pdf: Bank payment with Success/Blocked/Disputed tiles, invalid promo error
 *
 * DOM observations:
 *   - Payment method tabs: "link" (Stripe Link) and "Bank"
 *   - Stripe Link section: card row showing last 4 digits + "Use this card" button
 *   - Bank grid: 2x4 tiles (Success, Blocked, Disputed, Bank Non-OAuth,
 *                           Bank OAuth, Down Scheduled, Down Unscheduled, Down Error)
 *   - Order summary sidebar: package name, center, date/time, total price
 *   - Promo code input + "Apply Code" button
 *   - "Invalid Promo Code" error appears inline in red next to the input
 *   - "$5 back when you pay by bank" green banner when Bank tab is selected
 *   - "Back" and "Continue" navigation buttons
 */
class PaymentPage(factory: DriverFactory) : BasePage(factory) {

    // ── XPath locators ────────────────────────────────────────────────────────

    private val stepIndicator =
        "//*[contains(normalize-space(text()),'Reserve your appointment')]"

    private val pageHeading =
        "//*[self::h1 or self::h2]" +
            "[contains(normalize-space(text()),'Reserve your appointment')]"

    // ── Order summary sidebar ─────────────────────────────────────────────────

    /**
     * Total price in the order summary sidebar.
     * Targets the dollar value that is a sibling of the "Total" label.
     */
    private val orderTotalPrimary =
        "//*[normalize-space(text())='Total']/" +
            "following-sibling::*[contains(text(),'\$')]"

    private val orderTotalFallback =
        "//*[normalize-space(text())='Total']/..//*[contains(text(),'\$')]"

    private val summaryPackageName =
        "//*[contains(@class,'summary') or contains(@class,'sidebar') " +
            "or contains(@class,'order')]//h2"

    // ── Promo code ────────────────────────────────────────────────────────────

    private val promoCodeInput =
        "//input[@placeholder='Promo Code' " +
            "or @placeholder='promo code' " +
            "or @aria-label='Promo Code' " +
            "or @name='promoCode' " +
            "or @id='promoCode']"

    private val applyCodeButton =
        "//button[normalize-space(text())='Apply Code' " +
            "or normalize-space(text())='Apply' " +
            "or normalize-space(text())='apply code']"

    /**
     * Inline error shown when an invalid promo code is entered.
     * From screenshot: appears in red next to the promo code input field.
     */
    val invalidPromoCodeError =
        "//*[contains(normalize-space(text()),'Invalid Promo Code') " +
            "or contains(normalize-space(text()),'invalid promo') " +
            "or contains(normalize-space(text()),'Promo code not found') " +
            "or contains(normalize-space(text()),'Invalid code')]"

    private val promoAppliedSuccess =
        "//*[contains(normalize-space(text()),'applied') " +
            "or contains(normalize-space(text()),'discount') " +
            "or contains(@class,'promo-success')]"

    // ── Payment method tabs ───────────────────────────────────────────────────

    private val linkPaymentTab =
        "//*[normalize-space(text())='link' " +
            "or .//*[normalize-space(text())='link']]" +
            "[@role='tab' or contains(@class,'tab') or contains(@class,'payment')]"

    private val bankPaymentTab =
        "//label[.//*[normalize-space(text())='Bank'] " +
            "or normalize-space(text())='Bank'] | " +
            "//*[@role='tab' and contains(normalize-space(text()),'Bank')] | " +
            "//*[.//*[normalize-space(text())='Bank'] " +
            "and (contains(@class,'tab') or contains(@class,'payment-method'))]"

    // ── Stripe Link card row ──────────────────────────────────────────────────

    private val visaCardRow =
        "//*[contains(normalize-space(text()),'Visa Credit') " +
            "or contains(normalize-space(text()),'Visa Debit') " +
            "or .//*[contains(@class,'visa') or contains(@src,'visa')]]"

    private val useThisCardButton =
        "//button[contains(normalize-space(text()),'Use this card')]"

    // ── Bank payment tiles ────────────────────────────────────────────────────

    /**
     * Targets a specific bank tile by its label text.
     * Available labels from screenshot:
     *   Success, Blocked, Disputed, Bank (Non-OAuth),
     *   Bank (OAuth), Down (Scheduled), Down (Unscheduled), Down (Error)
     */
    private fun bankTile(label: String) =
        "//*[contains(normalize-space(text()),'$label') " +
            "and (ancestor::*[contains(@class,'bank') " +
            "or contains(@class,'grid') " +
            "or contains(@class,'tile') " +
            "or contains(@class,'payment')])]"

    /**
     * Green banner shown when Bank payment method is selected.
     * Text from screenshot: "Get $5 back when you pay by bank. See terms"
     */
    private val bankCashbackBanner =
        "//*[contains(normalize-space(text()),'\$5 back') " +
            "or contains(normalize-space(text()),'Get \$5') " +
            "or contains(normalize-space(text()),'pay by bank')]"

    // ── Navigation buttons ────────────────────────────────────────────────────

    private val continueButton =
        "//button[normalize-space(text())='Continue']"

    private val continueButtonDisabled =
        "//button[normalize-space(text())='Continue' " +
            "and (@disabled or contains(@class,'disabled'))]"

    private val backButton =
        "//button[normalize-space(text())='Back']"

    // ── Navigation ────────────────────────────────────────────────────────────

    fun waitUntilLoaded() {
        findByXPath(stepIndicator)
    }

    fun isOnPaymentPage(): Boolean =
        existsByXPath(stepIndicator) ||
            currentUrl().contains("reserve-appointment")

    // ── Promo code actions ────────────────────────────────────────────────────

    fun enterPromoCode(code: String) = typeInto(promoCodeInput, code)

    fun clickApplyCode() {
        waitClickable(applyCodeButton).click()
        // Brief pause for the validation response to render inline
        Thread.sleep(800)
    }

    /** Convenience method — enters the code and clicks Apply in one call. */
    fun applyPromoCode(code: String) {
        enterPromoCode(code)
        clickApplyCode()
    }

    // ── Order summary ─────────────────────────────────────────────────────────

    /**
     * Returns the total price string from the sidebar, e.g. "$999".
     * Tries multiple XPath patterns and returns the first non-blank result.
     */
    fun getOrderTotal(): String {
        val patterns = listOf(
            orderTotalPrimary,
            orderTotalFallback,
            "//*[normalize-space(text())='Total']/following-sibling::*[1]",
            "//*[normalize-space(text())='Total']/parent::*//*[contains(text(),'\$')]"
        )
        for (xpath in patterns) {
            val value = findAllByXPath(xpath)
                .firstOrNull()
                ?.text
                ?.trim()
                .orEmpty()
            if (value.isNotBlank()) return value
        }
        return findByXPath(orderTotalPrimary).text.trim()
    }

    fun getSummaryPackageName(): String =
        findByXPath(summaryPackageName).text.trim()

    // ── Payment method selection ──────────────────────────────────────────────

    fun selectLinkPayment() {
        scrollAndClick(linkPaymentTab)
    }

    fun selectBankPayment() {
        scrollAndClick(bankPaymentTab)
        // Wait for the bank tile grid to render before returning
        findByXPath(bankTile("Success"))
    }

    /** Clicks a specific bank tile by label, e.g. "Success", "Blocked". */
    fun selectBankTile(label: String) {
        scrollAndClick(bankTile(label))
    }

    fun clickUseThisCard() {
        waitClickable(useThisCardButton).click()
        waitForPageLoad()
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    fun clickContinue() {
        waitClickable(continueButton).click()
        waitForPageLoad()
    }

    fun clickBack() {
        waitClickable(backButton).click()
        waitForPageLoad()
    }

    // ── State checks ─────────────────────────────────────────────────────────

    fun isInvalidPromoErrorDisplayed(): Boolean =
        existsByXPath(invalidPromoCodeError)

    fun getInvalidPromoErrorText(): String =
        findByXPath(invalidPromoCodeError).text.trim()

    fun isPromoAppliedSuccessfully(): Boolean =
        existsByXPath(promoAppliedSuccess)

    fun isBankCashbackBannerVisible(): Boolean =
        existsByXPath(bankCashbackBanner)

    fun getBankCashbackBannerText(): String =
        findByXPath(bankCashbackBanner).text.trim()

    fun isBankTileVisible(label: String): Boolean =
        existsByXPath(bankTile(label))

    fun isVisaCardRowVisible(): Boolean =
        existsByXPath(visaCardRow)

    fun getVisaCardLastFour(): String {
        val text = findByXPath(visaCardRow).text
        return Regex("""\d{4}""").findAll(text).lastOrNull()?.value ?: ""
    }

    fun isPromoCodeInputVisible(): Boolean =
        existsByXPath(promoCodeInput)

    fun isContinueEnabled(): Boolean =
        existsByXPath(continueButton) &&
            !existsByXPath(continueButtonDisabled)
}
