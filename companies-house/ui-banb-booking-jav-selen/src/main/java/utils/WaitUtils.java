package utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class WaitUtils {

    WebDriver driver;
    WebDriverWait wait;

    // Constructor to initialize WebDriver and Wait
    public WaitUtils(WebDriver driver) {
        this.driver = driver;
        // Wait up to 10 seconds for an element to appear
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // Wait until element is visible
    public WebElement waitForVisibility(By locator) {
        System.out.println("Waiting for visibility of element: " + locator.toString());
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    // Wait until element is clickable
    public WebElement waitForClickability(By locator) {
        System.out.println("Waiting for clickability of element: " + locator.toString());
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    // Wait until element is present in the DOM
    public WebElement waitForPresence(By locator) {
        System.out.println("Waiting for presence of element in DOM: " + locator.toString());
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }
}
