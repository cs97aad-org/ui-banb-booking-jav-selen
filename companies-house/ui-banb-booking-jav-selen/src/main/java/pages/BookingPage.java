package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utils.WaitUtils;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import org.openqa.selenium.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;



/**
 * Booking (reservation) page after clicking "Book now".
 * We keep locators resilient by relying on visible text.
 */
public class BookingPage {

    private final WebDriver driver;
    private final WaitUtils wait;

    // Headers / sections
    private final By bookThisRoomHeader   = By.xpath("//*[self::h1 or self::h2 or self::h3][contains(.,'Book This Room')]");
    private final By roomDescriptionBlock = By.xpath("//*[self::h2 or self::h3 or self::h4 or contains(@class,'card-title')][contains(.,'Room Description')]");
    private final By perNightText         = By.xpath("//*[contains(.,'per night')]");
    private final By priceSummaryBlock    = By.xpath("//*[contains(translate(., 'PRICE', 'price'),'price') and contains(translate(., 'SUMMARY', 'summary'),'summary')]");

    // Dates often appear as text or readonly inputs; we search broadly
    private final By anyDateText = By.xpath("//*[contains(text(),'/') or contains(text(),'-')][not(self::script)]");

    // Calendar (React Big Calendar)
    private final By calendarContainer   = By.cssSelector(".rbc-calendar");
    private final By monthViewContainer  = By.cssSelector(".rbc-month-view");
    // The selected range shows as an event with text 'Selected'
    private final By selectedEventLabels = By.xpath("//div[contains(@class,'rbc-event-content')][normalize-space()='Selected']");

    // Booking form inputs (right-hand column)
    private final By firstNameInput   = By.cssSelector("input[name='firstname']");
    private final By lastNameInput    = By.cssSelector("input[name='lastname']");
    private final By emailInput       = By.cssSelector("input[name='email']");
    private final By phoneInput       = By.cssSelector("input[name='phone']");

    // Reserve Now button
    private final By reserveNowButton = By.xpath("//button[normalize-space()='Reserve Now']");

    // --- Booking confirmation panel (right column card) ---
    private final By bookingConfirmedHeader = By.xpath("//*[self::h2 or self::h3][contains(normalize-space(),'Booking Confirmed')]");
    private final By confirmedDatesStrong   = By.xpath("//div[contains(@class,'booking-card')]//p[contains(@class,'text-center')]//strong"); // [0]=start [1]=end
    private final By returnHomeButton       = By.xpath("//a[normalize-space()='Return home' or normalize-space()='Return Home']");

    // Card that contains the Booking Confirmed content
    private final By bookingConfirmedCard =
            By.xpath("//*[self::h2 or self::h3][contains(normalize-space(),'Booking Confirmed')]/ancestor::div[contains(@class,'card')][1]");

    // --- Locators (keep names meaningful) ---
    private final By btnReserveNow = By.xpath("//button[normalize-space()='Reserve Now']");

    // Covers most bootstrap-ish validation patterns: inline small/div, or alert summary list.
    private final By anyValidationMsg = By.cssSelector(
            "form .invalid-feedback, " +        // inline under inputs
                    "form .text-danger, " +             // common inline class
                    "form .alert-danger, " +            // summary block
                    "form .alert-danger li, " +         // summary list items
                    "form small.text-danger"            // small red text
    );

    // --- Locators ---
    private final By btnCancel = By.xpath("//button[normalize-space()='Cancel']");
    private final By hdrBookThisRoom = By.xpath("//*[self::h2 or self::h3][normalize-space()='Book This Room']");


    public BookingPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WaitUtils(driver);
    }

    public boolean isLoaded() {
        System.out.println("Waiting for 'Book This Room' header...");
        wait.waitForVisibility(bookThisRoomHeader);
        boolean bookHeaderVisible  = driver.findElement(bookThisRoomHeader).isDisplayed();
        System.out.println("Book This Room' visible: " + bookHeaderVisible );
        return bookHeaderVisible ;
    }

    public boolean isRoomDescriptionVisible() {
        System.out.println("🔎 Checking 'Room Description' section...");
        wait.waitForVisibility(roomDescriptionBlock);
        return driver.findElement(roomDescriptionBlock).isDisplayed();
    }

    public boolean isPerNightShown() {
        System.out.println("🔎 Checking 'per night' text...");
        wait.waitForVisibility(perNightText);
        return driver.findElement(perNightText).isDisplayed();
    }

    public boolean isPriceSummaryVisible() {
        System.out.println("🔎 Checking 'Price Summary'...");
        wait.waitForVisibility(priceSummaryBlock);
        return driver.findElement(priceSummaryBlock).isDisplayed();
    }

    /**
     * Returns page text that contains dates so step defs can assert expected values.
     * We keep this generic because the site may render dates as dd/MM/yyyy or yyyy-MM-dd.
     */
    public String grabAllVisibleDateText() {
        System.out.println("Grabbing visible text nodes that look like dates...");
        return driver.findElements(anyDateText)
                .stream()
                .map(e -> e.getText().trim())
                .filter(t -> !t.isEmpty())
                .reduce("", (a, b) -> a + " | " + b);
    }

    /**
     * A simple contains check that the selected room title is somewhere on the page.
     * e.g., "Single" or "Single Room".
     */
    public boolean pageContainsRoomTitle(String roomTitle) {
        String text = driver.getPageSource();
        return text.toLowerCase().contains(roomTitle.toLowerCase());
    }

    public String currentUrl() {
        return driver.getCurrentUrl();
    }

    private String getQueryParam(String name) {
        try {
            String query = new URL(driver.getCurrentUrl()).getQuery(); // e.g. checkin=2025-11-09&checkout=2025-11-10
            if (query == null) return null;
            for (String part : query.split("&")) {
                String[] kv = part.split("=", 2);
                String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
                if (key.equals(name)) {
                    return kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : "";
                }
            }
        } catch (Exception e) {
            System.out.println("Could not parse URL query: " + e.getMessage());
        }
        return null;
    }
    public String getCheckInFromUrl()  { return getQueryParam("checkin"); }
    public String getCheckOutFromUrl() { return getQueryParam("checkout"); }


    public void scrollCalendarIntoView() {
        System.out.println("Scrolling calendar into view...");
        WebElement cal = wait.waitForVisibility(calendarContainer);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", cal);
        try { Thread.sleep(200); } catch (InterruptedException ignored) {}
    }

    /** Returns how many 'Selected' event chips are visible (the highlighted range). */
    public int countSelectedRangeMarkers() {
        wait.waitForVisibility(calendarContainer);
        List<WebElement> events = driver.findElements(selectedEventLabels);
        System.out.println(" 'Selected' event markers found: " + events.size());
        return events.size();
    }

    /** Returns true if a month-cell button exists with the given day number (e.g., 9, 10). */
    /*
    public boolean isDayNumberVisibleInMonth(int dayNumber) {
        // limit search within the month view
        By dayBtn = By.xpath("//div[contains(@class,'rbc-month-view')]//button[@type='button' and normalize-space()='" + dayNumber + "']");
        List<WebElement> els = driver.findElements(dayBtn);
        boolean visible = !els.isEmpty() && els.get(0).isDisplayed();
        System.out.println("Day button '" + dayNumber + "' visible in month grid: " + visible);
        return visible;
    } */


    /** Returns true if a month-cell button exists with the given day number.
     *  Handles zero-padded labels like 03, 09 in React Big Calendar. */
    public boolean isDayNumberVisibleInMonth(int dayNumber) {
        wait.waitForVisibility(monthViewContainer);

        String d1 = String.valueOf(dayNumber);          // e.g., "9"
        String d2 = String.format("%02d", dayNumber);   // e.g., "09"

        By dayBtn = By.xpath(
                "//div[contains(@class,'rbc-month-view')]//button[@type='button' and " +
                        "(normalize-space()='" + d1 + "' or normalize-space()='" + d2 + "')]"
        );

        List<WebElement> els = driver.findElements(dayBtn); // find matching buttons
        boolean visible = !els.isEmpty() && els.get(0).isDisplayed(); //
        System.out.println("Day button match for '" + d1 + "' or '" + d2 + "': " + visible);

        // Helpful debug: if not found, print all visible day labels
        if (!visible) {
            List<WebElement> all = driver.findElements(
                    By.xpath("//div[contains(@class,'rbc-month-view')]//button[@type='button']")
            );
            String labels = all.stream().map(e -> e.getText().trim()).collect(Collectors.joining(", "));
            System.out.println("🧾 Visible day buttons: [" + labels + "]");
        }
        return visible;
    }

    public void scrollToReserveButton() {
        System.out.println("🧭 Scrolling to 'Reserve Now' button...");
        WebElement button = wait.waitForVisibility(reserveNowButton);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", button);
        try { Thread.sleep(200); } catch (InterruptedException ignored) {}
    }

    public void clickReserveNow() {
        System.out.println("🖱️ Clicking 'Reserve Now'…");
        wait.waitForClickability(reserveNowButton).click();
        System.out.println("✅ Clicked 'Reserve Now'.");
    }

    public boolean areBookingFieldsVisible() {
        System.out.println("Verifying booking form fields are visible…");
        boolean fn = wait.waitForVisibility(firstNameInput).isDisplayed();
        boolean ln = wait.waitForVisibility(lastNameInput).isDisplayed();
        boolean em = wait.waitForVisibility(emailInput).isDisplayed();
        boolean ph = wait.waitForVisibility(phoneInput).isDisplayed();
        System.out.println("🧾 Visibility → first:" + fn + ", last:" + ln + ", email:" + em + ", phone:" + ph);
        return fn && ln && em && ph;
    }

    /*
    private void clearAndType(By locator, String text) {
        WebElement field = wait.waitForVisibility(locator);
        field.click();
        field.clear();
        field.sendKeys(text);
        System.out.println("✍🏽 Typed '" + text + "' into: " + locator);
    }
    */

    private void clearAndType(By locator, String text) {
        WebElement field = bringIntoView(locator);

        // Try a normal click; if the navbar intercepts, JS-click.
        try {
            wait.waitForClickability(locator).click();
        } catch (ElementClickInterceptedException e) {
            System.out.println("Click intercepted by sticky header. Using JS click for: " + locator);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", field);
        }

        // Cross-platform select-all + clear
        boolean isMac = System.getProperty("os.name").toLowerCase().contains("mac");
        Keys modKey = isMac ? Keys.COMMAND : Keys.CONTROL;

        field.sendKeys(Keys.chord(modKey, "a"));
        field.sendKeys(Keys.BACK_SPACE);
        field.clear();

        // Type text; if framework ignores it, set via JS as a fallback and fire 'input' event
        try {
            field.sendKeys(text);
            field.sendKeys(Keys.TAB);
        } catch (Exception ignored) {
            System.out.println("sendKeys failed, setting value via JS for: " + locator);
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input',{bubbles:true}));",
                    field, text
            );
        }

        System.out.println("Typed '" + text + "' into: " + locator);
    }

    public void fillBookingForm(String firstName, String lastName, String emailAddr, String phoneNumber) {
        System.out.println("Filling booking form with generated values…");
        clearAndType(firstNameInput, firstName);
        clearAndType(lastNameInput,  lastName);
        clearAndType(emailInput,     emailAddr);
        clearAndType(phoneInput,     phoneNumber);
        System.out.println("Booking form filled.");
    }

    private WebElement bringIntoView(By locator) {
        WebElement element = wait.waitForVisibility(locator);
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center', inline:'nearest'});", element);
        try { Thread.sleep(200); } catch (InterruptedException ignored) {}
        return element;
    }

    public boolean isBookingConfirmedVisible() {
        System.out.println("🔎 Waiting for 'Booking Confirmed' panel...");
        wait.waitForVisibility(bookingConfirmedHeader);
        return driver.findElement(bookingConfirmedHeader).isDisplayed();
    }

    public String getConfirmedStartIso() {
        wait.waitForVisibility(confirmedDatesStrong);
        return driver.findElements(confirmedDatesStrong).get(0).getText().trim(); // e.g., 2025-11-04
    }

    public String getConfirmedEndIso() {
        wait.waitForVisibility(confirmedDatesStrong);
        return driver.findElements(confirmedDatesStrong).get(1).getText().trim(); // e.g., 2025-11-05
    }

    /** Returns [checkinISO, checkoutISO] by regex-parsing the confirmation card text.
     *  Works whether the UI shows one or two <strong> nodes. */

    public String[] getConfirmedDatesIso() {
        wait.waitForVisibility(bookingConfirmedCard);
        WebElement card = driver.findElement(bookingConfirmedCard);
        String cardText = card.getText();
        System.out.println("📋 Confirmation card text:\n" + cardText);

        // 1) Primary: regex over the card text (matches both "YYYY-MM-DD - YYYY-MM-DD" and two lines)
        Pattern iso = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
        Matcher m = iso.matcher(cardText);
        ArrayList<String> found = new ArrayList<>();
        while (m.find()) found.add(m.group());

        if (found.size() >= 2) {
            System.out.println("Parsed ISO dates from card text: " + found.get(0) + " → " + found.get(1));
            return new String[]{ found.get(0), found.get(1) };
        }

        // 2) Fallback: try the <strong> nodes (sometimes the app renders two)
        var strongs = driver.findElements(confirmedDatesStrong);
        if (strongs.size() >= 2) {
            String s = strongs.get(0).getText().trim();
            String e = strongs.get(1).getText().trim();
            System.out.println("Parsed ISO dates from <strong> nodes: " + s + " → " + e);
            return new String[]{ s, e };
        } else if (strongs.size() == 1) {
            String s = strongs.get(0).getText().trim();
            System.out.println("Only one <strong> node found: " + s);
            return new String[]{ s, "" };
        }

        System.out.println("Could not find two ISO dates on the confirmation card.");
        return new String[0];
    }


    public boolean isReturnHomeShown() {
        wait.waitForVisibility(returnHomeButton);
        return driver.findElement(returnHomeButton).isDisplayed();
    }

    public void clickReturnHome() {
        System.out.println("🖱️ Clicking 'Return home'...");
        WebElement btn = wait.waitForVisibility(returnHomeButton);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
        try { wait.waitForClickability(returnHomeButton).click(); }
        catch (ElementClickInterceptedException e) {
            System.out.println("⚠️ Intercepted — using JS click for Return home");
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
        System.out.println("✅ 'Return home' clicked.");
    }

    // Returns all validation texts currently visible in the booking form
    public List<String> getValidationMessages() {
        System.out.println("🧾 Collecting validation messages...");
        // Short, local wait until any validation appears
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(5))
                .until(d -> !d.findElements(anyValidationMsg).isEmpty());

        List<String> messages = driver.findElements(anyValidationMsg).stream()
                .map(WebElement::getText)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .distinct()
                .toList();

        System.out.println("👉 Found validation messages: " + messages);
        return messages;
    }

    // --- Actions ---
    public void clickCancelBooking() {
        System.out.println("🛑 Clicking 'Cancel' to abort booking...");
        WebElement btn = wait.waitForVisibility(btnCancel);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,-120);"); // avoid sticky header
        try {
            wait.waitForClickability(btnCancel).click();
        } catch (ElementClickInterceptedException e) {
            System.out.println("⚠️ Interception detected; using JS click.");
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }
}

