package com.ezra.qa.runner

import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
    features = ["classpath:features"],
    glue = [
        "com.ezra.qa.steps.api",
        "com.ezra.qa.steps.ui"
    ],
    plugin = ["pretty", "html:build/reports/cucumber/cucumber.html"],
    tags = "not @skip"
)
class TestRunner
