package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.ElementClickInterceptedException;
import utils.WaitUtils;
import org.openqa.selenium.Keys;

import java.util.List;

/**
 * HomePage class (Page Object Model)
 * Represents the Shady Meadows B&B home page.
 * Contains locators and methods for actions and verifications.
 */

public class HomePage {
    WebDriver driver;
    WaitUtils waitUtils;

    //  Updated locator using CSS Selector (more reliable than XPath)
    private By headerText = By.cssSelector("h1.display-4.fw-bold.mb-4");

    // Locators for Check Availability section
    private By bookingSection        = By.cssSelector("section#booking");
    private By bookingSectionTitle   = By.cssSelector("section#booking h3.card-title");

    private By checkInInput = By.xpath("//label[@for='checkin']/following-sibling::div//input");
    private By checkOutInput = By.xpath("//label[@for='checkout']/following-sibling::div//input");
    private By checkAvailabilityBtn = By.xpath("//button[contains(text(),'Check Availability')]");
    private By ourRoomsHeader = By.cssSelector("h2.display-5");

    // ----- Our Rooms grid -----
    private By roomsSection    = By.cssSelector("section#rooms");
    private By roomCards       = By.cssSelector("section#rooms .room-card");  // each card
    private By roomTitleInCard = By.cssSelector("h5.card-title");             // e.g., Single / Double / Suite
    private By roomPriceInCard = By.cssSelector(".fw-bold.fs-5");             // e.g., £100
    private By bookNowInCard   = By.cssSelector("a.btn.btn-primary");         // "Book now" link

    // --- add near your other locators ---
    private By navContactLink = By.cssSelector("a[href='#contact']");


    // Constructor
    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver);  // Initialize WaitUtils
    }

    // Navigate to the main home page
    public void navigateToHomePage() {
        System.out.println("Navigating to Shady Meadows B&B...");
        driver.get("https://automationintesting.online/");
        driver.manage().window().maximize();
        System.out.println("Page loaded successfully!");

        // Optional: Handle cookie consent popup if present
        try {
            By cookieBtn = By.cssSelector("button.fc-cta-consent");
            if (driver.findElements(cookieBtn).size() > 0) {
                driver.findElement(cookieBtn).click();
                System.out.println("Cookie consent banner dismissed!");
            }
        } catch (Exception e) {
            System.out.println("No cookie banner found (skipping).");
        }
    }

    // Get page title
    public String getPageTitle() {
        String title = driver.getTitle();
        System.out.println("Page Title: " + title);
        return title;
    }

    //  Wait for header and verify visibility
    public boolean verifyHeaderText() {
        System.out.println("Checking if header text is visible...");
        waitUtils.waitForVisibility(headerText); // Wait for header
        boolean visible = driver.findElement(headerText).isDisplayed();
        System.out.println("Header displayed: " + visible);
        return visible;
    }

    // Wait for and get the header text
    public String getHeaderText() {
        System.out.println("🔍 Waiting for header text...");
        waitUtils.waitForVisibility(headerText);
        String header = driver.findElement(headerText).getText().trim();
        System.out.println("Header text found: " + header);
        return header;
    }


    // Click the "Check Availability" button safely
    public void clickCheckAvailability() {
        System.out.println(" Attempting to click 'Check Availability' button...");

        try {
            // Wait for button to become clickable
            WebElement button = waitUtils.waitForClickability(checkAvailabilityBtn);

            // Scroll the button into view (important on Mac/Chrome)
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", button);
            Thread.sleep(500); // short pause for smooth scroll

            // Try normal click first
            button.click();
            System.out.println("'Check Availability' button clicked successfully!");

        } catch (ElementClickInterceptedException e) {
            System.out.println("⚠️ Click intercepted! Trying JavaScript click instead...");
            WebElement button = driver.findElement(checkAvailabilityBtn);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
            System.out.println("Fallback JS click worked!");

        } catch (Exception e) {
            System.out.println("Error while clicking 'Check Availability': " + e.getMessage());
        }
    }

    public String getSectionText() {
        System.out.println("🔍 Waiting for and getting section header text...");
        waitUtils.waitForVisibility(ourRoomsHeader);
        String sectionHeader = driver.findElement(ourRoomsHeader).getText().trim();
        System.out.println("Section header text found: " + sectionHeader);
        return sectionHeader;
    }

    // Scroll to the booking section ("Check Availability & Book Your Stay")
    public void scrollToBookingSection() {
        System.out.println("Scrolling to 'Check Availability & Book Your Stay' section...");
        WebElement section = waitUtils.waitForVisibility(bookingSection);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", section);
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        System.out.println("Reached the booking section.");
    }

    // Return the booking section title text (e.g., "Check Availability & Book Your Stay")
    public String getBookingSectionTitle() {
        waitUtils.waitForVisibility(bookingSectionTitle);
        String text = driver.findElement(bookingSectionTitle).getText().trim();
        System.out.println("Booking section title: " + text);
        return text;
    }

    // Return values of the date fields (used to assert defaults)
    public String getDefaultCheckIn() {
        String val = waitUtils.waitForVisibility(checkInInput).getAttribute("value").trim();
        System.out.println("Default Check-In value: " + val);
        return val;
    }
    public String getDefaultCheckOut() {
        String val = waitUtils.waitForVisibility(checkOutInput).getAttribute("value").trim();
        System.out.println("Default Check-Out value: " + val);
        return val;
    }

    // Enter check-in and check-out dates
    public void enterCheckInAndOutDates(String checkIn, String checkOut) {
//        System.out.println("Entering check-in/check-out dates...");
//
//        WebElement checkInField = waitUtils.waitForVisibility(checkInInput);
//        checkInField.clear();
//        checkInField.sendKeys(checkIn);
//
//        WebElement checkOutField = waitUtils.waitForVisibility(checkOutInput);
//        checkOutField.clear();
//        checkOutField.sendKeys(checkOut);
//
//        System.out.println("Entered Check-In: " + checkIn + " | Check-Out: " + checkOut);

        System.out.println("Setting Check-In and Check-Out dates...");
        clearAndTypeDate(checkInInput, checkIn);

        // Ensure date picker overlay doesn't steal the next typing
        try { driver.findElement(checkInInput).sendKeys(Keys.ESCAPE); } catch (Exception ignored) {}

        clearAndTypeDate(checkOutInput, checkOut);
        try { driver.findElement(checkOutInput).sendKeys(Keys.ESCAPE); } catch (Exception ignored) {}

        System.out.println("Dates entered → Check-In: " + checkIn + " | Check-Out: " + checkOut);
    }

  /*  // ---------------------------------------------
    // Select-all + clear + type for date fields (Mac & Windows safe)
    private void clearAndTypeDate(By inputLocator, String dateStr) {
        WebElement field = waitUtils.waitForVisibility(inputLocator);

        // Focus the field so keys go to the right place
        field.click();

        // Use CMD on Mac, CTRL elsewhere
        boolean isMac = System.getProperty("os.name").toLowerCase().contains("mac");
        Keys modifier = isMac ? Keys.COMMAND : Keys.CONTROL;

        // Robust clear sequence (React inputs often ignore simple clear())
        field.sendKeys(Keys.chord(modifier, "a"));   // select all
        field.sendKeys(Keys.BACK_SPACE);             // delete selection
        field.clear();                                // belt-and-braces

        // Type the target date and tab out to commit value
        field.sendKeys(dateStr);
        field.sendKeys(Keys.TAB);                    // blur/close calendar

        System.out.println("✅ Set date '" + dateStr + "' in: " + inputLocator);
    }
*/

    // Select-all + clear + type for date fields (handles sticky navbar + datepicker overlay)
    private void clearAndTypeDate(By inputLocator, String dateStr) {
        System.out.println("🖱️ Focusing date field: " + inputLocator);

        WebElement field = bringIntoView(inputLocator);

        // Try a normal click; if intercepted, use JS click
        try {
            waitUtils.waitForClickability(inputLocator).click();
        } catch (ElementClickInterceptedException e) {
            System.out.println("⚠️ Click intercepted by sticky header. Using JS click...");
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", field);
        }

        // Cross-platform select-all and clear
        boolean isMac = System.getProperty("os.name").toLowerCase().contains("mac");
        Keys mod = isMac ? Keys.COMMAND : Keys.CONTROL;

        field.sendKeys(Keys.chord(mod, "a"));   // select all
        field.sendKeys(Keys.BACK_SPACE);        // delete
        field.clear();                          // belt & braces

        // Type target date and TAB to commit/close the calendar
        field.sendKeys(dateStr);
        field.sendKeys(Keys.TAB);

        // Extra: close any open datepicker overlay
        try { field.sendKeys(Keys.ESCAPE); } catch (Exception ignored) {}

        System.out.println("✅ Set date '" + dateStr + "' in: " + inputLocator);
    }


    // Center the element in the viewport so the sticky navbar won't cover it
    private WebElement bringIntoView(By locator) {
        WebElement el = waitUtils.waitForVisibility(locator);
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center', inline:'nearest'});", el);
        try { Thread.sleep(250); } catch (InterruptedException ignored) {}
        return el;
    }

    /**
     * Validates room cards under "Our Rooms".
     * A "valid" card has a visible title, a non-empty price, and a visible Book button.
     * @return how many valid room cards were found (for assertion >= 1)
     */
    public int countValidRoomCards() {
        System.out.println("Looking for available rooms in 'Our Rooms' section...");
        waitUtils.waitForVisibility(roomsSection);

        List<WebElement> cards = driver.findElements(roomCards);
        System.out.println("🧾 Found " + cards.size() + " room card(s).");

        int validCount = 0;

        for (int i = 0; i < cards.size(); i++) {
            WebElement card = cards.get(i);
            System.out.println("— Checking card #" + (i + 1));

            boolean hasTitle = false, hasPrice = false, hasBook = false;
            String titleText = "", priceText = "", bookText = "";

            try {
                WebElement titleEl = card.findElement(roomTitleInCard);
                hasTitle = titleEl.isDisplayed() && !titleEl.getText().trim().isEmpty();
                titleText = titleEl.getText().trim();
            } catch (Exception ignored) {}

            try {
                WebElement priceEl = card.findElement(roomPriceInCard);
                hasPrice = priceEl.isDisplayed() && !priceEl.getText().trim().isEmpty();
                priceText = priceEl.getText().trim();
            } catch (Exception ignored) {}

            try {
                WebElement bookEl = card.findElement(bookNowInCard);
                hasBook = bookEl.isDisplayed();
                bookText = bookEl.getText().trim();
            } catch (Exception ignored) {}

            if (hasTitle && hasPrice && hasBook) {
                validCount++;
                System.out.println("Valid room → Title: " + titleText + " | Price: " + priceText + " | Button: " + bookText);
            } else {
                System.out.println("Incomplete card → title:" + hasTitle + ", price:" + hasPrice + ", book:" + hasBook);
            }
        }

        System.out.println("Valid room cards found: " + validCount);
        return validCount;
    }

    /**
     * Clicks the first visible "Book now" button and returns the room title from that card (e.g., "Single").
     * click a specific room type can later be parameterised
     */

    public String clickFirstBookNowAndCaptureRoomTitle() {
        System.out.println("🖱️ Looking for a room card with a 'Book now' button...");
        waitUtils.waitForVisibility(roomsSection);
        List<WebElement> cards = driver.findElements(roomCards);

        for (int i = 0; i < cards.size(); i++) {
            WebElement card = cards.get(i);
            try {
                WebElement titleEl = card.findElement(roomTitleInCard);
                String title = titleEl.getText().trim();      // "Single", "Double", "Suite"
                WebElement bookBtn = card.findElement(bookNowInCard);
                if (bookBtn.isDisplayed()) {
                    ((JavascriptExecutor) driver)
                            .executeScript("arguments[0].scrollIntoView({block:'center'});", bookBtn);
                    Thread.sleep(200);
                    try {
                        bookBtn.click();
                    } catch (ElementClickInterceptedException e) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", bookBtn);
                    }
                    System.out.println("Clicked 'Book now' for room: " + title);
                    return title;
                }
            } catch (Exception ignored) { /* try next card */ }
        }
        throw new RuntimeException("No room card with a visible 'Book now' button was found.");
    }

    /*
    public void clickContactNav() {
        System.out.println("🧭 Clicking 'Contact' in navbar...");
        WebElement link = waitUtils.waitForVisibility(navContactLink);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", link);
        waitUtils.waitForClickability(navContactLink).click();
        System.out.println(" Navbar 'Contact' clicked.");
    } */

    public void clickContactNav() {
        System.out.println("Clicking 'Contact' in navbar...");

        // Locators (keep them as fields if you prefer)
        By navbar         = By.cssSelector("nav.navbar");
        By toggler        = By.cssSelector("button.navbar-toggler");
        By contactLink    = By.xpath("//nav//a[normalize-space()='Contact' or contains(@href,'#contact')]");

        // 1) Navbar present
        waitUtils.waitForVisibility(navbar);

        // 2) If menu is collapsed, expand it (mobile/tablet widths)
        boolean linkVisible = driver.findElements(contactLink).stream()
                .findFirst().map(WebElement::isDisplayed).orElse(false);
        if (!linkVisible) {
            var togglers = driver.findElements(toggler);
            if (!togglers.isEmpty() && togglers.get(0).isDisplayed()) {
                System.out.println("☰ Expanding collapsed navbar...");
                try { togglers.get(0).click(); }
                catch (ElementClickInterceptedException e) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", togglers.get(0));
                }
            }
        }

        // 3) Now wait for the link to be visible & clickable
        WebElement link = waitUtils.waitForVisibility(contactLink);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", link);
        try {
            waitUtils.waitForClickability(contactLink).click();
        } catch (ElementClickInterceptedException e) {
            System.out.println("Intercepted by sticky header/overlay; using JS click.");
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", link);
        }
        System.out.println("Navbar 'Contact' clicked.");
    }

}
