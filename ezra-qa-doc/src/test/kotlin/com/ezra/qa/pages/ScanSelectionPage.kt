package com.ezra.qa.pages

import com.ezra.qa.config.TestConfig
import com.ezra.qa.driver.DriverFactory

/**
 * Page Object for "Review your Scan" — Step 1 of the booking flow.
 * URL: /book-scan/select-plan
 *
 * Screenshot reference: booking_scan_plan.pdf
 *
 * DOM observations:
 *   - Four plan cards: MRI Scan, MRI Scan with Spine,
 *     MRI Scan with Skeletal and Neurological Assessment, Heart CT Scan
 *   - Each card shows plan name, "Available at $X" price, and Affirm monthly price
 *   - Selected card gets a highlighted border (yellow/gold in screenshot)
 *   - Heart CT Scan has an (i) info icon — restricted plan
 *   - "Continue" button is disabled until a plan is selected
 *   - "Cancel" button exits the booking flow entirely
 *   - Step breadcrumb shows "Review your plan" as active (step 1 of 3)
 *
 * XPath strategy:
 *   - Plan cards: div containing both the plan name heading AND "Available at"
 *     price text — structural relationship, not a class name
 *   - Selected state: aria-selected, aria-pressed, or selected/active class
 *     on the card wrapper
 *   - Pricing: sibling or child of the plan name within the card
 */
class ScanSelectionPage(factory: DriverFactory) : BasePage(factory) {

    // ── XPath locators ────────────────────────────────────────────────────────

    private val stepIndicator =
        "//*[contains(normalize-space(text()),'Review your plan')]"

    private val pageHeading =
        "//*[contains(normalize-space(text()),'Review your Scan')]"

    /**
     * The clickable card container for a given plan name.
     * Targets a div that contains BOTH the plan name text AND a price —
     * this structural double-condition avoids matching unrelated elements.
     */
    private fun planCard(planName: String) =
        "//div[.//*[contains(normalize-space(text()),'$planName')] " +
            "and .//*[contains(normalize-space(text()),'Available at')]]"

    /**
     * Price text within a specific plan card.
     * e.g. "Available at $999"
     */
    private fun planPriceText(planName: String) =
        "${planCard(planName)}" +
            "//*[contains(normalize-space(text()),'Available at')]"

    /**
     * Affirm monthly price within a specific plan card.
     * e.g. "As low as $63 / month"
     */
    private fun planMonthlyPrice(planName: String) =
        "${planCard(planName)}" +
            "//*[contains(normalize-space(text()),'/ month')]"

    /**
     * Selected state check — the portal applies one of these when a card
     * is active: aria-selected, aria-pressed, or a selected/active/ring class.
     */
    private fun selectedPlanCard(planName: String) =
        "//div[.//*[contains(normalize-space(text()),'$planName')] " +
            "and (contains(@class,'selected') " +
            "or contains(@class,'active') " +
            "or contains(@class,'ring') " +
            "or contains(@class,'border-yellow') " +
            "or @aria-selected='true' " +
            "or .//*[@aria-pressed='true'])]"

    /**
     * Info icon on restricted plans (Heart CT Scan).
     * Targets button or svg with an info-related label or class.
     */
    private fun planInfoIcon(planName: String) =
        "${planCard(planName)}" +
            "//*[contains(@aria-label,'info') " +
            "or contains(@class,'info') " +
            "or self::button[contains(@class,'info')]]"

    private val continueButton =
        "//button[contains(normalize-space(text()),'Continue')]"

    private val continueButtonDisabled =
        "//button[contains(normalize-space(text()),'Continue') " +
            "and (@disabled or contains(@class,'disabled'))]"

    private val cancelButton =
        "//button[contains(normalize-space(text()),'Cancel')]"

    // ── Navigation ────────────────────────────────────────────────────────────

    fun navigate() {
        driver.get("${TestConfig.portalUrl}/book-scan/select-plan")
        waitForPageLoad()
    }

    fun waitUntilLoaded() {
        findByXPath(pageHeading)
        findByXPath(stepIndicator)
    }

    fun isOnScanSelectionPage(): Boolean =
        existsByXPath(pageHeading) ||
            currentUrl().contains("select-plan")

    // ── Actions ───────────────────────────────────────────────────────────────

    /**
     * Clicks the plan card for the given plan name.
     * Uses scrollAndClick because lower cards may be off-screen on load.
     */
    fun selectPlan(planName: String) {
        scrollAndClick(planCard(planName))
    }

    fun clickContinue() {
        waitClickable(continueButton).click()
        waitForPageLoad()
    }

    fun clickCancel() {
        waitClickable(cancelButton).click()
        waitForPageLoad()
    }

    fun clickPlanInfoIcon(planName: String) {
        waitClickable(planInfoIcon(planName)).click()
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    /**
     * Returns the full price string, e.g. "Available at $999".
     */
    fun getPlanPriceText(planName: String): String =
        findByXPath(planPriceText(planName)).text.trim()

    /**
     * Extracts just the dollar amount from the price text.
     * "Available at $999" → "$999"
     */
    fun getPlanPriceAmount(planName: String): String {
        val full = getPlanPriceText(planName)
        return Regex("""\$[\d,]+""").find(full)?.value ?: full
    }

    fun getMonthlyPriceText(planName: String): String =
        findByXPath(planMonthlyPrice(planName)).text.trim()

    /**
     * Returns the names of all visible plan cards on the page.
     * Used to verify all four plans are displayed.
     */
    fun getVisiblePlanNames(): List<String> {
        val cards = findAllByXPath(
            "//div[.//*[contains(normalize-space(text()),'Available at')]]"
        )
        return cards.mapNotNull { card ->
            card.findElements(
                org.openqa.selenium.By.xpath(
                    ".//*[self::h2 or self::h3 or self::h4]"
                )
            ).firstOrNull()?.text?.trim()?.takeIf { it.isNotBlank() }
        }.distinct()
    }

    // ── State checks ──────────────────────────────────────────────────────────

    /**
     * Checks whether the given plan card is in a selected/highlighted state.
     *
     * Primary check: looks for aria/class-based selected indicators.
     * Fallback: inspects the card element's class and style attributes
     * directly for ring or border styling applied by the portal.
     */
    fun isPlanSelected(planName: String): Boolean {
        if (existsByXPath(selectedPlanCard(planName))) return true

        return try {
            val card = findByXPath(planCard(planName))
            val cls   = card.getAttribute("class") ?: ""
            val style = card.getAttribute("style") ?: ""
            cls.contains("ring") ||
                cls.contains("border-2") ||
                style.contains("border") ||
                style.contains("outline")
        } catch (_: Exception) {
            false
        }
    }

    fun isContinueButtonEnabled(): Boolean =
        existsByXPath(continueButton) &&
            !existsByXPath(continueButtonDisabled)

    fun isPlanInfoIconVisible(planName: String): Boolean =
        existsByXPath(planInfoIcon(planName))
}
