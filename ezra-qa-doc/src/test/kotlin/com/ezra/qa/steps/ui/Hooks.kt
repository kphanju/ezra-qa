package com.ezra.qa.steps.ui

import com.ezra.qa.context.ScenarioContext
import io.cucumber.java8.En
import io.cucumber.java8.Scenario
import org.openqa.selenium.OutputType
import org.openqa.selenium.TakesScreenshot

/**
 * Cucumber lifecycle hooks using the java8 lambda style.
 *
 * cucumber-java8 registers hooks via After{} and AfterStep{} method calls
 * inside an init block — NOT via @After/@AfterStep annotations.
 * Annotations belong to cucumber-java (a separate dependency not in this project).
 */
class Hooks(private val context: ScenarioContext) : En {

    init {

        /**
         * Runs after every scenario.
         * Quits the browser if it was opened during the scenario.
         * Safe to call even if no browser was launched (API-only scenarios).
         */
        After { _: Scenario ->
            context.quitDriver()
        }

        /**
         * After any failed step, captures a PNG screenshot and attaches
         * it to the Cucumber HTML/JSON report for debugging.
         * Only fires if the driver was actually initialised this scenario.
         */
        AfterStep { scenario: Scenario ->
            if (scenario.isFailed) {
                runCatching {
                    val screenshot = (context.driverFactory.driver as TakesScreenshot)
                        .getScreenshotAs(OutputType.BYTES)
                    scenario.attach(
                        screenshot,
                        "image/png",
                        "Failure screenshot: ${scenario.name}"
                    )
                }
            }
        }
    }
}
