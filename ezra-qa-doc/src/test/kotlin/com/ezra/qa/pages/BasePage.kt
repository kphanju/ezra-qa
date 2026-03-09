package com.ezra.qa.pages


import com.ezra.qa.driver.DriverFactory
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebElement
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.ExpectedConditions
import java.time.Duration

/**
 * Base class for all Page Objects.
 *
 * Centralises XPath-based element lookup and explicit wait helpers so
 * individual page objects stay focused on their own DOM structure.
 *
 * All element lookups use XPath. Rationale:
 *   - The portal uses dynamic CSS class names that change on build
 *   - XPath lets us target stable attributes: data-testid, aria-label,
 *     element text content, and structural relationships
 */
abstract class BasePage(protected val factory: DriverFactory) {

    protected val driver get() = factory.driver
    protected val wait   get() = factory.wait

    // ─────────────────────────────────────────────────────────────────────────
    // Core XPath finders
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Waits until element is visible, then returns it.
     * Throws TimeoutException if not found within the configured timeout.
     */
    protected fun findByXPath(xpath: String): WebElement =
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)))

    /**
     * Waits until element is visible AND enabled (safe to click).
     */
    protected fun waitClickable(xpath: String): WebElement =
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)))

    /**
     * Returns true if the element exists in the DOM right now.
     * Does NOT wait — safe to use for boolean checks.
     */
    protected fun existsByXPath(xpath: String): Boolean =
        driver.findElements(By.xpath(xpath)).isNotEmpty()

    /**
     * Waits up to [seconds] for the element to appear in the DOM.
     * Use when element may not be visible yet but will be soon.
     */
    protected fun waitPresent(xpath: String, seconds: Long = 15L): WebElement {
        val shortWait = org.openqa.selenium.support.ui.WebDriverWait(
            driver, Duration.ofSeconds(seconds)
        )
        return shortWait.until(
            ExpectedConditions.presenceOfElementLocated(By.xpath(xpath))
        )
    }

    /**
     * Waits for element to disappear from view.
     * Useful for loading spinners and overlays.
     */
    protected fun waitInvisible(xpath: String) {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(xpath)))
    }

    /**
     * Returns all matching elements without waiting.
     * Returns empty list if none found — never throws.
     */
    protected fun findAllByXPath(xpath: String): List<WebElement> =
        driver.findElements(By.xpath(xpath))

    // ─────────────────────────────────────────────────────────────────────────
    // Interaction helpers
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Clears the field and types the given text.
     * Waits for element to be clickable first.
     */
    protected fun typeInto(xpath: String, text: String) {
        val el = waitClickable(xpath)
        el.clear()
        el.sendKeys(text)
    }

    /**
     * Clicks via JavaScript — use when a normal click is intercepted
     * by an overlay, sticky header, or animation.
     */
    protected fun jsClick(xpath: String) {
        val el = findByXPath(xpath)
        (driver as JavascriptExecutor).executeScript("arguments[0].click();", el)
    }

    /**
     * Scrolls the element into view then clicks it.
     * Use for elements below the fold (e.g. lower plan cards).
     */
    protected fun scrollAndClick(xpath: String) {
        val el = findByXPath(xpath)
        (driver as JavascriptExecutor)
            .executeScript("arguments[0].scrollIntoView(true);", el)
        el.click()
    }

    /**
     * Moves the mouse over an element.
     * Use for tooltips and hover-triggered menus.
     */
    protected fun hoverOver(xpath: String) {
        val el = findByXPath(xpath)
        Actions(driver).moveToElement(el).perform()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Reusable XPath expression builders
    //
    // Use these in page objects instead of writing raw XPath strings.
    // Keeps locators readable and reduces duplication.
    // ─────────────────────────────────────────────────────────────────────────

    /** Button by its exact visible text. */
    protected fun xpathButtonByText(text: String) =
        "//button[normalize-space(text())='$text']"

    /** Button whose text contains the given substring. */
    protected fun xpathButtonContains(text: String) =
        "//button[contains(normalize-space(text()),'$text')]"

    /** Any element by exact text content. */
    protected fun xpathByText(tag: String, text: String) =
        "//$tag[normalize-space(text())='$text']"

    /** Any element whose text contains a substring. */
    protected fun xpathContainsText(tag: String, text: String) =
        "//$tag[contains(normalize-space(text()),'$text')]"

    /** Element by data-testid attribute — most stable locator when available. */
    protected fun xpathTestId(testId: String) =
        "//*[@data-testid='$testId']"

    /** Input by its placeholder text. */
    protected fun xpathInputByPlaceholder(placeholder: String) =
        "//input[@placeholder='$placeholder']"

    /** Element by aria-label attribute. */
    protected fun xpathAriaLabel(label: String) =
        "//*[@aria-label='$label']"

    // ─────────────────────────────────────────────────────────────────────────
    // Page-level utilities
    // ─────────────────────────────────────────────────────────────────────────

    fun currentUrl(): String = driver.currentUrl

    fun pageTitle(): String = driver.title

    /**
     * Waits until the browser URL contains the given fragment.
     * Use to confirm navigation after a button click.
     */
    fun waitForUrlContaining(fragment: String) {
        wait.until(ExpectedConditions.urlContains(fragment))
    }

    /**
     * Best-effort wait for loading spinners to disappear.
     * Does not fail the test if no spinner is found.
     */
    fun waitForPageLoad() {
        val spinnerXpath =
            "//*[contains(@class,'loading') or contains(@class,'spinner')]"
        runCatching { waitInvisible(spinnerXpath) }
    }
}
