package utils;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

public final class UiActions {

    private UiActions() {}

    public static WebElement bringIntoViewCenter(WebDriver driver, WaitUtils wait, By locator) {
        WebElement el = wait.waitForVisibility(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", el);
        try { Thread.sleep(150); } catch (InterruptedException ignored) {}
        return el;
    }

    public static void safeClick(WebDriver driver, WaitUtils wait, By locator) {
        WebElement el = bringIntoViewCenter(driver, wait, locator);
        try {
            wait.waitForClickability(locator).click();
        } catch (ElementClickInterceptedException e) {
            System.out.println("Intercepted. Using JS click for: " + locator);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        }
    }

    public static void clearAndType(WebDriver driver, WaitUtils wait, By locator, String text) {
        WebElement el = bringIntoViewCenter(driver, wait, locator);
        try { wait.waitForClickability(locator).click(); }
        catch (ElementClickInterceptedException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        }

        boolean mac = System.getProperty("os.name").toLowerCase().contains("mac");
        Keys mod = mac ? Keys.COMMAND : Keys.CONTROL;
        el.sendKeys(Keys.chord(mod, "a"));
        el.sendKeys(Keys.BACK_SPACE);
        el.clear();
        el.sendKeys(text);
    }
}
