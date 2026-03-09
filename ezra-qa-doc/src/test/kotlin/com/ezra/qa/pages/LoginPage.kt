package com.ezra.qa.pages

import com.ezra.qa.config.TestConfig
import com.ezra.qa.driver.DriverFactory

/**
 * Page Object for the Ezra member portal login page.
 * URL: https://myezra-staging.ezra.com/sign-in
 *
 * XPath strategy:
 *   - Email/password inputs targeted by type attribute (stable across rebuilds)
 *   - Submit button targeted by text content
 *   - Error message targeted by role="alert" or text content
 */
class LoginPage(factory: DriverFactory) : BasePage(factory) {

    // ── XPath locators ────────────────────────────────────────────────────────

    private val emailInput =
        "//input[@type='email' or @name='email' or @id='email']"

    private val passwordInput =
        "//input[@type='password' or @name='password']"

    private val signInButton =
        "//button[contains(normalize-space(text()),'Sign in') " +
            "or contains(normalize-space(text()),'Log in') " +
            "or contains(normalize-space(text()),'Continue')]"

    private val errorMessage =
        "//*[@role='alert' " +
            "or contains(@class,'error') " +
            "or contains(normalize-space(text()),'Invalid') " +
            "or contains(normalize-space(text()),'incorrect')]"

    private val loggedInMarker =
        "//*[contains(@href,'/dashboard') " +
            "or contains(@href,'/home') " +
            "or contains(normalize-space(text()),'Book a scan') " +
            "or contains(normalize-space(text()),'Welcome')]"

    // ── Actions ───────────────────────────────────────────────────────────────

    fun navigate() {
        driver.get("${TestConfig.portalUrl}/sign-in")
        waitForPageLoad()
    }

    fun enterEmail(email: String) = typeInto(emailInput, email)

    fun enterPassword(password: String) = typeInto(passwordInput, password)

    fun clickSignIn() {
        waitClickable(signInButton).click()
        waitForPageLoad()
    }

    fun loginAs(email: String, password: String) {
        navigate()
        enterEmail(email)
        enterPassword(password)
        clickSignIn()
    }

    // ── Assertions ────────────────────────────────────────────────────────────

    fun isLoginErrorDisplayed(): Boolean = existsByXPath(errorMessage)

    fun getLoginErrorText(): String = findByXPath(errorMessage).text.trim()

    fun isLoggedIn(): Boolean = existsByXPath(loggedInMarker)

    fun waitUntilLoggedIn() {
        waitForUrlContaining("/dashboard")
        findByXPath(loggedInMarker)
    }
}
