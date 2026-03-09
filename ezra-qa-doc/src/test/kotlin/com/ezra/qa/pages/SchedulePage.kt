package com.ezra.qa.pages

import com.ezra.qa.driver.DriverFactory

/**
 * Page Object for "Schedule your scan" — Step 2 of the booking flow.
 * URL: /book-scan/schedule  (exact path may vary)
 *
 * DOM observations:
 *   - Location search input or list of center cards
 *   - Each center card shows name, address, and available slot count
 *   - Time slot grid with buttons per available time (e.g. "3:00 PM", "9:00 AM")
 *   - A date picker or calendar for navigating between days
 *   - "Back" and "Continue" navigation buttons at the bottom
 *   - Step breadcrumb shows "Schedule your scan" as active (step 2 of 3)
 *   - Loading skeleton visible while slots are being fetched from the API
 *
 * XPath strategy:
 *   - Centers: div cards containing the center name text within a
 *     card/center/location wrapper — structural relationship
 *   - Time slots: buttons within a slot/calendar grid identified by time text
 *   - Selected slot: aria-pressed="true", aria-selected="true",
 *     or selected/active class on the button
 *   - No-availability message: text content match
 */
class SchedulePage(factory: DriverFactory) : BasePage(factory) {

    // ── XPath locators ────────────────────────────────────────────────────────

    private val stepIndicator =
        "//*[contains(normalize-space(text()),'Schedule your scan')]"

    private val pageHeading =
        "//*[contains(normalize-space(text()),'Schedule') " +
            "and (self::h1 or self::h2 or contains(@class,'heading'))]"

    // ── Center / location locators ────────────────────────────────────────────

    /**
     * Center card containing the given center name.
     * Targets a div that wraps both the name AND has card/center/location
     * styling — avoids matching plain text mentions of the center name.
     */
    private fun centerCard(centerName: String) =
        "//div[.//*[contains(normalize-space(text()),'$centerName')] " +
            "and (contains(@class,'card') " +
            "or contains(@class,'center') " +
            "or contains(@class,'location') " +
            "or @role='button' " +
            "or @tabindex)]"

    /**
     * Fallback: any clickable element that contains the center name text.
     * Used when the card wrapper does not carry a recognisable class.
     */
    private fun centerByName(centerName: String) =
        "//*[contains(normalize-space(text()),'$centerName') " +
            "and (self::button " +
            "or self::div[@role='button'] " +
            "or ancestor::button)]"

    /** Location / city / zip search input */
    private val locationSearchInput =
        "//input[@placeholder[contains(.,'city') " +
            "or contains(.,'location') " +
            "or contains(.,'zip') " +
            "or contains(.,'search')] " +
            "or @aria-label[contains(.,'location') " +
            "or contains(.,'city') " +
            "or contains(.,'search')]]"

    // ── Time slot locators ────────────────────────────────────────────────────

    /**
     * A specific available time slot button by its displayed time text.
     * e.g. "3:00 PM", "9:00 AM", "10:30 AM"
     * Only matches non-disabled buttons.
     */
    private fun timeSlotByText(time: String) =
        "//button[contains(normalize-space(text()),'$time') " +
            "and not(@disabled) " +
            "and not(contains(@class,'disabled'))]"

    /**
     * All available (non-disabled) time slot buttons.
     * Targets buttons inside a slot/calendar/time container.
     */
    private val availableTimeSlots =
        "//button[not(@disabled) " +
            "and not(contains(@class,'disabled')) " +
            "and (ancestor::*[contains(@class,'slot') " +
            "or contains(@class,'slots') " +
            "or contains(@class,'calendar') " +
            "or contains(@class,'time')] " +
            "or contains(@class,'slot') " +
            "or contains(@class,'time-slot'))]"

    /**
     * Checks whether a specific time slot is in a selected state.
     * The portal applies one of these when a slot is active:
     * aria-pressed, aria-selected, or a selected/active class.
     */
    private fun selectedTimeSlot(time: String) =
        "//button[contains(normalize-space(text()),'$time') " +
            "and (@aria-pressed='true' " +
            "or @aria-selected='true' " +
            "or contains(@class,'selected') " +
            "or contains(@class,'active'))]"

    /** Any selected slot — used when we don't know which time was picked. */
    private val anySelectedSlot =
        "//button[@aria-pressed='true' " +
            "or @aria-selected='true' " +
            "or (contains(@class,'selected') " +
            "and (contains(@class,'slot') " +
            "or contains(@class,'time')))]"

    // ── State / feedback locators ─────────────────────────────────────────────

    /**
     * Shown when a center has no available slots for the selected date.
     */
    private val noAvailabilityMessage =
        "//*[contains(normalize-space(text()),'no availability') " +
            "or contains(normalize-space(text()),'No available') " +
            "or contains(normalize-space(text()),'No slots') " +
            "or contains(normalize-space(text()),'fully booked')]"

    /**
     * Loading skeleton or spinner shown while slots are being fetched.
     * Waiting for this to disappear prevents clicking before slots render.
     */
    private val loadingIndicator =
        "//*[contains(@class,'skeleton') " +
            "or contains(@class,'loading') " +
            "or contains(@class,'spinner') " +
            "or @role='progressbar']"

    // ── Navigation buttons ────────────────────────────────────────────────────

    private val continueButton =
        "//button[contains(normalize-space(text()),'Continue')]"

    private val continueButtonDisabled =
        "//button[contains(normalize-space(text()),'Continue') " +
            "and (@disabled or contains(@class,'disabled'))]"

    private val backButton =
        "//button[contains(normalize-space(text()),'Back')]"

    // ── Page load ─────────────────────────────────────────────────────────────

    fun waitUntilLoaded() {
        findByXPath(stepIndicator)
        // Wait for any loading skeletons to disappear before interacting
        runCatching { waitInvisible(loadingIndicator) }
    }

    fun isOnSchedulePage(): Boolean =
        existsByXPath(stepIndicator) ||
            currentUrl().contains("schedule")

    // ── Location / center actions ─────────────────────────────────────────────

    fun searchLocation(query: String) {
        typeInto(locationSearchInput, query)
        runCatching { waitInvisible(loadingIndicator) }
    }

    /**
     * Selects a center by name.
     * Tries the structural card XPath first, falls back to plain text match
     * if the card wrapper does not carry a recognisable class.
     */
    fun selectCenter(centerName: String) {
        try {
            scrollAndClick(centerCard(centerName))
        } catch (_: Exception) {
            scrollAndClick(centerByName(centerName))
        }
        runCatching { waitInvisible(loadingIndicator) }
    }

    // ── Time slot actions ─────────────────────────────────────────────────────

    /**
     * Selects a time slot by its displayed text, e.g. "3:00 PM".
     * Falls back to selecting the first available slot if the exact
     * time is not found — useful when slot availability is dynamic.
     */
    fun selectTimeSlot(time: String) {
        try {
            scrollAndClick(timeSlotByText(time))
        } catch (_: Exception) {
            println("[SchedulePage] Slot '$time' not found — selecting first available")
            selectFirstAvailableSlot()
        }
    }

    /**
     * Selects the first available slot without caring which time it is.
     * Use this in tests where the exact time is not the thing being asserted.
     */
    fun selectFirstAvailableSlot() {
        val slots = findAllByXPath(availableTimeSlots)
        check(slots.isNotEmpty()) {
            "No available time slots found on schedule page — " +
                "check center availability or date selection"
        }
        slots.first().click()
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

    // ── Getters ───────────────────────────────────────────────────────────────

    /**
     * Returns the text of all visible available time slot buttons.
     * Useful for asserting that at least one slot is displayed.
     */
    fun getAvailableSlotTimes(): List<String> =
        findAllByXPath(availableTimeSlots)
            .map { it.text.trim() }
            .filter { it.isNotBlank() }

    /**
     * Returns the text of the currently selected slot.
     * Throws if no slot is selected yet.
     */
    fun getSelectedSlotTime(): String =
        findByXPath(anySelectedSlot).text.trim()

    // ── State checks ──────────────────────────────────────────────────────────

    fun isTimeSlotSelected(time: String): Boolean =
        existsByXPath(selectedTimeSlot(time))

    fun hasAnySlotSelected(): Boolean =
        existsByXPath(anySelectedSlot)

    fun hasNoAvailabilityMessage(): Boolean =
        existsByXPath(noAvailabilityMessage)

    fun isContinueEnabled(): Boolean =
        existsByXPath(continueButton) &&
            !existsByXPath(continueButtonDisabled)
}
