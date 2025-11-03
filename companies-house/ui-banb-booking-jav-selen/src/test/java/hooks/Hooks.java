package hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import utils.BaseTest;

public class Hooks extends BaseTest {

    @Before
    public void beforeScenario() {
        System.out.println("======= BEFORE SCENARIO: starting WebDriver =======");
        setup();   // launches browser and sets implicit wait
    }

    @After
    public void afterScenario(Scenario scenario) {
        System.out.println("======= AFTER SCENARIO: closing WebDriver =======");
        // (optional) add screenshot capture here if scenario.isFailed()
        tearDown(); // closes browser once, after the scenario
    }
}