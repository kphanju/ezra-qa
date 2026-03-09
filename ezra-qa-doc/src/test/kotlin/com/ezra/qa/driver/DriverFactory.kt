package com.ezra.qa.driver

import com.ezra.qa.config.TestConfig
import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

class DriverFactory {

    val driver: WebDriver by lazy { createDriver() }

    val wait: WebDriverWait by lazy {
        WebDriverWait(driver, Duration.ofSeconds(TestConfig.uiTimeoutSeconds))
    }

    private fun createDriver(): WebDriver {
        WebDriverManager.chromedriver().setup()
        val options = ChromeOptions().apply {
            if (TestConfig.headless) addArguments("--headless=new")
            addArguments(
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--window-size=1440,900",
                "--disable-blink-features=AutomationControlled"
            )
        }
        return ChromeDriver(options).also {
            it.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(TestConfig.uiTimeoutSeconds))
        }
    }

    fun quit() = runCatching { driver.quit() }
}
