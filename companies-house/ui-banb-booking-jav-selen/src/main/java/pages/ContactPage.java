package pages;

import org.openqa.selenium.*;
import utils.WaitUtils;

public class ContactPage {
    private final WebDriver driver;
    private final WaitUtils wait;

    // ====== Locators ======
    private final By contactSection     = By.cssSelector("section#contact");
    private final By contactHeading     = By.xpath("//section[@id='contact']//h3[contains(normalize-space(),'Send Us a Message')]");

    // Inputs (support both testid and id to be resilient)
    private final By nameInput          = By.cssSelector("input[data-testid='ContactName'],  #name");
    private final By emailInput         = By.cssSelector("input[data-testid='ContactEmail'], #email");
    private final By phoneInput         = By.cssSelector("input[data-testid='ContactPhone'], #phone");
    private final By subjectInput       = By.cssSelector("input[data-testid='ContactSubject'], #subject");
    private final By messageTextarea    = By.cssSelector("textarea[data-testid='ContactDescription'], textarea#description");

    private final By submitButton       = By.xpath("//section[@id='contact']//button[normalize-space()='Submit']");
    private final By btnSubmit          = By.xpath("//section[@id='contact']//button[normalize-space()='Submit']");

    // Confirmation (after submit)
    private final By confirmHeading     = By.xpath("//h3[starts-with(normalize-space(),'Thanks for getting in touch')]");
    // The subject appears in a bold-style paragraph just under the “we’ll get back to you” line
    private final By confirmSubjectBold = By.xpath("//h3[starts-with(normalize-space(),'Thanks for getting in touch')]/following::p[contains(translate(@style,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'font-weight')][1]");

    public ContactPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WaitUtils(driver);
    }

    // ====== Actions / Assertions ======
    public void waitForContactSection() {
        System.out.println("🔎 Waiting for Contact section...");
        wait.waitForVisibility(contactSection);
        wait.waitForVisibility(contactHeading);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'start'});",
                driver.findElement(contactSection));
    }

    public boolean areAllFieldsVisible() {
        System.out.println("🧾 Verifying all Contact fields are visible…");
        return wait.waitForVisibility(nameInput).isDisplayed()
                && wait.waitForVisibility(emailInput).isDisplayed()
                && wait.waitForVisibility(phoneInput).isDisplayed()
                && wait.waitForVisibility(subjectInput).isDisplayed()
                && wait.waitForVisibility(messageTextarea).isDisplayed();
    }

    private void clearAndType(By locator, String text) {
        WebElement field = wait.waitForVisibility(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", field);
        try { wait.waitForClickability(locator).click(); }
        catch (ElementClickInterceptedException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", field);
        }
        // cross-platform select all
        boolean mac = System.getProperty("os.name").toLowerCase().contains("mac");
        Keys mod = mac ? Keys.COMMAND : Keys.CONTROL;
        field.sendKeys(Keys.chord(mod, "a"));
        field.sendKeys(Keys.BACK_SPACE);
        field.clear();
        field.sendKeys(text);
        System.out.println("✍🏽 Typed '" + text + "' into: " + locator);
    }

    public void fillContactForm(String fullName, String emailAddr, String phoneNum, String subjectText, String messageText) {
        System.out.println("🧰 Filling Contact form with generated values…");
        clearAndType(nameInput,       fullName);
        clearAndType(emailInput,      emailAddr);
        clearAndType(phoneInput,      phoneNum);
        clearAndType(subjectInput,    subjectText);
        clearAndType(messageTextarea, messageText);
        System.out.println("Contact form filled.");
    }

    /*
    public void clickSubmit() {
        System.out.println("🖱️ Clicking Submit...");
        WebElement btn = wait.waitForVisibility(submitButton);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
        wait.waitForClickability(submitButton).click();
        System.out.println("Submit clicked.");
    } */

    public void clickSubmit() {
        System.out.println("🖱️ Clicking Submit...");

        WebElement btn = wait.waitForVisibility(btnSubmit);

        // 1) bring to view, then nudge up so sticky elements don't cover it
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,-120);");  // small offset

        try {
            wait.waitForClickability(btnSubmit).click();                 // 2) normal click
            System.out.println("Submit clicked (native).");
        } catch (ElementClickInterceptedException e1) {
            System.out.println("Intercepted. Trying Actions click...");
            try {
                new org.openqa.selenium.interactions.Actions(driver)
                        .moveToElement(btn, 3, 3).click().perform();     // 3) Actions click
                System.out.println("Submit clicked (Actions).");
            } catch (Exception e2) {
                System.out.println("Still intercepted. Using JS click.");
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn); // 4) JS fallback
                System.out.println("Submit clicked (JS).");
            }
        }
    }

    public String getConfirmationHeading() {
        wait.waitForVisibility(confirmHeading);
        return driver.findElement(confirmHeading).getText().trim();
    }

    public String getConfirmationSubject() {
        wait.waitForVisibility(confirmSubjectBold);
        return driver.findElement(confirmSubjectBold).getText().trim();
    }
}

