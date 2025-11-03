package stepdefinitions;

import io.cucumber.java.PendingException;
import io.cucumber.java.en.*;
import pages.HomePage;
import utils.BaseTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;


/**
 * Step Definitions for "Check Availability" scenario.
 * Connects Gherkin steps to actual Selenium actions.
 * Uses HomePage (Page Object Model) and BaseTest (WebDriver setup).
 */

public class CheckAvailabilitySteps extends BaseTest {

    HomePage homePage;

    @Given("User is on the home page")
    public void user_is_on_the_home_page() {
        System.out.println("============== TEST STARTED: CHECK AVAILABILITY ==============");
        BaseTest.setup();       // start browser once
        BaseTest.openHome();    // navigate using centralised baseUrl
        homePage = new HomePage(BaseTest.driver);
//        homePage = new HomePage(driver);
//        homePage.navigateToHomePage();
    }

    @Then("Page title should be {string}")
    public void page_title_should_be(String expectedTitle) {
        System.out.println("Checking page title...");
        String actualTitle = homePage.getPageTitle();
        assertTrue(actualTitle.contains(expectedTitle), "Page title mismatch!");
        System.out.println("Page title verified successfully!");
    }

    @And("User is navigated to the Home page showing the header {string}")
    @Then("Header text should be {string}")
    public void header_text_should_be(String expectedHeader) {
        System.out.println("Verifying header text matches expected value...");
        String actualHeader = homePage.getHeaderText();
        assertEquals(expectedHeader,  actualHeader, "Header text does not match expected value!");
        System.out.println("Header text verified successfully!");
    }


    @And("User scrolls to the {string} section")
    public void user_scrolls_to_section(String sectionName) {
        // The parameter is for readability in the scenario; we scroll to the booking section.
        System.out.println("Scrolling to section: " + sectionName);
        homePage.scrollToBookingSection();
    }

    /**
     * Asserts the booking section title text using a getter and compares with param from feature.
     * Example: "Check Availability & Book Your Stay"
     */

    @And("Booking section title should be {string}")
    public void booking_section_title_should_be(String expectedTitle) {
        String actualTitle = homePage.getBookingSectionTitle();
        assertEquals( expectedTitle, actualTitle, "Booking section title mismatch!");
        System.out.println("Booking section title verified: " + actualTitle);
    }

    /**
     * Verifies defaults:
     * - Check-in is today
     * - Check-out is tomorrow
     * Uses system timezone and dd/MM/yyyy to match the site format.
     */
    @And("the check-in date is defaulted to today’s date, and checkout is defaulted to tomorrow’s date")
    public void verify_default_dates() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        LocalDate tomorrow = today.plusDays(1);

        String expectedCheckIn  = today.format(fmt);
        String expectedCheckOut = tomorrow.format(fmt);

        String actualCheckIn  = homePage.getDefaultCheckIn();
        String actualCheckOut = homePage.getDefaultCheckOut();

        assertEquals (expectedCheckIn, actualCheckIn, "Default Check-In date is not today!");
        assertEquals(expectedCheckOut, actualCheckOut, "Default Check-Out date is not tomorrow!");

        System.out.println("Default dates verified → Check-In: " + actualCheckIn +
                " | Check-Out: " + actualCheckOut);
    }

    @When("User enters a valid check-in date {string} days from today, and a check-out of {string} nights")
    public void user_enters_checkin_with_offset_and_checkout_nights(String offsetDaysStr, String nightsStr) {
        System.out.println("Computing dates using offsetDays=" + offsetDaysStr + " and nights=" + nightsStr);

        // Parse numbers from the step
        int offsetDays = Integer.parseInt(offsetDaysStr.trim());
        int nights = Integer.parseInt(nightsStr.trim());

        // Basic guard rails for beginners (fail fast with a helpful message)
        if (nights < 1) {
            throw new IllegalArgumentException("Number of nights must be at least 1 (got: " + nights + ").");
        }

        // Use system timezone and dd/MM/yyyy to match the site
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate today   = LocalDate.now(ZoneId.systemDefault());
        LocalDate checkIn = today.plusDays(offsetDays);   // today + offset
        LocalDate checkOut = checkIn.plusDays(nights);    // check-in + nights

        String checkInStr  = checkIn.format(fmt);
        String checkOutStr = checkOut.format(fmt);

        System.out.println("Final dates → Check-In: " + checkInStr + " | Check-Out: " + checkOutStr +
                " (nights=" + nights + ")");

        // Fill the fields via POM
        homePage.enterCheckInAndOutDates(checkInStr, checkOutStr);

        // sanity check: read back the values after typing
        String typedIn  = homePage.getDefaultCheckIn();
        String typedOut = homePage.getDefaultCheckOut();
        assertEquals(checkInStr, typedIn, "Check-In value typed does not match computed date!");
        assertEquals( checkOutStr, typedOut, "Check-Out value typed does not match computed date!");
        System.out.println("Date fields match the computed values.");
    }

    @When("User clicks on Check Availability")
    public void user_clicks_on_check_availability() {
        System.out.println("Clicking on 'Check Availability'...");
        homePage.clickCheckAvailability();
    }

    @Then("User should see {string} section")
    public void user_should_see_section(String sectionTitle) {
        System.out.println(" Checking if user navigated to section: " + sectionTitle);
        String actualSectionTitle = homePage.getSectionText();
        assertEquals(sectionTitle, actualSectionTitle, "Section title mismatch! Expected:");
    }


    @And("User should see available rooms for the selected dates -  room type, Single, Double and Suite rooms; price and the Book Now button.")
    public void user_should_see_available_rooms_for_selected_dates() {
        System.out.println("🔍 Verifying available rooms with title, price and Book button...");
        int valid = homePage.countValidRoomCards();
        assertTrue(valid >= 1, "No valid available rooms were displayed for the selected dates.");
        System.out.println("Assertion passed: " + valid + " valid room card(s) displayed.");

        System.out.println("============== TEST ENDED SUCCESSFULLY ==============");
    }

}
