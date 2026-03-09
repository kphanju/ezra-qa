package com.ezra.qa.pages


import com.ezra.qa.driver.DriverFactory

/**
 * Page Object for the booking confirmation screen.
 * Shown after a successful payment completes.
 *
 * DOM observations:
 *   - A success heading confirming the appointment is booked
 *   - Appointment details: package name, center, date/time
 *   - Optional "Add to calendar" button
 *   - Optional "Go to dashboard" or "View appointment" link
 *
 * XPath strategy:
 *   - Confirmation heading: text content match on common confirmation phrases
 *   - Details: structural relationship within a confirmation/appointment container
 *   - Buttons: text content match
 */
class ConfirmationPage(factory: DriverFactory) : BasePage(factory) {

    // ── XPath locators ────────────────────────────────────────────────────────

    /**
     * The main confirmation heading.
     * Matches common phrases portals use after a successful booking.
     */
    private val confirmationHeading =
        "//*[contains(normalize-space(text()),'confirmed') " +
            "or contains(normalize-space(text()),'Confirmed') " +
            "or contains(normalize-space(text()),'booked') " +
            "or contains(normalize-space(text()),'Booked') " +
            "or contains(normalize-space(text()),'appointment is set') " +
            "or contains(normalize-space(text()),'See you') " +
            "or contains(normalize-space(text()),'You are scheduled') " +
            "or contains(normalize-space(text()),'successfully booked')]"

    /** Appointment date/time shown in the confirmation details block. */
    private val appointmentDate =
        "//*[contains(@class,'confirmation') " +
            "or contains(@class,'appointment') " +
            "or contains(@class,'booking-details')]" +
            "//*[contains(text(),'2026') " +
            "or contains(text(),'EDT') " +
            "or contains(text(),'EST') " +
            "or contains(text(),'AM') " +
            "or contains(text(),'PM')]"

    /** Package/scan name shown in the confirmation details block. */
    private val confirmationPackageName =
        "//*[contains(@class,'confirmation') " +
            "or contains(@class,'appointment') " +
            "or contains(@class,'booking-details')]" +
            "//*[contains(text(),'MRI') " +
            "or contains(text(),'Scan') " +
            "or contains(text(),'CT')]"

    /** "Add to calendar" button — optional, not all portals show this. */
    private val addToCalendarButton =
        "//button[contains(normalize-space(text()),'calendar') " +
            "or contains(normalize-space(text()),'Calendar') " +
            "or contains(normalize-space(text()),'Add to')]" +
            "| //a[contains(normalize-space(text()),'calendar') " +
            "or contains(normalize-space(text()),'Add to')]"

    /** Link back to the dashboard or appointment list. */
    private val viewDashboardLink =
        "//a[contains(normalize-space(text()),'Dashboard') " +
            "or contains(normalize-space(text()),'Go to dashboard') " +
            "or contains(normalize-space(text()),'View appointment') " +
            "or contains(@href,'/dashboard') " +
            "or contains(@href,'/appointments')]"

    // ── Navigation ────────────────────────────────────────────────────────────

    /**
     * Waits until the confirmation page is fully loaded.
     * First waits for the URL to update, then waits for the
     * confirmation heading to appear.
     */
    fun waitUntilLoaded() {
        runCatching { waitForUrlContaining("confirm") }
        findByXPath(confirmationHeading)
    }

    fun isOnConfirmationPage(): Boolean =
        currentUrl().contains("confirm") ||
            existsByXPath(confirmationHeading)

    // ── Actions ───────────────────────────────────────────────────────────────

    fun clickAddToCalendar() {
        waitClickable(addToCalendarButton).click()
    }

    fun clickGoToDashboard() {
        waitClickable(viewDashboardLink).click()
        waitForPageLoad()
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    fun getConfirmationHeadingText(): String =
        findByXPath(confirmationHeading).text.trim()

    fun getAppointmentDateText(): String =
        findByXPath(appointmentDate).text.trim()

    fun getConfirmedPackageName(): String =
        findByXPath(confirmationPackageName).text.trim()

    // ── State checks ─────────────────────────────────────────────────────────

    fun isConfirmationDisplayed(): Boolean =
        existsByXPath(confirmationHeading)

    fun isAddToCalendarVisible(): Boolean =
        existsByXPath(addToCalendarButton)

    fun isViewDashboardVisible(): Boolean =
        existsByXPath(viewDashboardLink)
}
