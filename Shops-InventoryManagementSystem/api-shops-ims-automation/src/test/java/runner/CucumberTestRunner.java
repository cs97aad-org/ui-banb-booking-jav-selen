package runner;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")  // path relative to /src/test/resources
@ConfigurationParameter(key = "cucumber.glue", value = "stepdefinitions")
@ConfigurationParameter(key = "cucumber.plugin", value = "pretty, html:target/cucumber-report.html")
@ConfigurationParameter(key = "cucumber.execution.monochrome", value = "true")
@ConfigurationParameter(key = "cucumber.filter.tags", value = "@AllTests and not @ignore_notInSwagger")
public class CucumberTestRunner {
}

