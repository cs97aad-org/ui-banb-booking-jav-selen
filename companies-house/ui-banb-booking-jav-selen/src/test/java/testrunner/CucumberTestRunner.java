package testrunner;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * RunCucumberTest
 * ---------------
 * Purpose: Tell JUnit 5 to execute Cucumber features as tests.
 *
 * OOP: Declarative configuration via annotations; no code required inside.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features") // looks in src/test/resources/features
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME,
        value = "pretty, html:target/cucumber-report.html, json:target/cucumber.json, junit:target/cucumber-reports/TEST-results.xml, summary"
)
public class CucumberTestRunner {
}
