package stepdefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import pages.BookingPage;
import pages.HomePage;
import utils.BaseTest;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

import net.datafaker.Faker;

import java.util.List;
import java.util.Locale;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.function.Executable;



public class BookRoomSteps extends BaseTest {

    private HomePage home;
    private BookingPage booking;

    // carry-over state from earlier steps
    // (your CheckAvailabilitySteps sets dates; we recompute here for simplicity)
    private String lastCheckIn;   // dd/MM/yyyy
    private String lastCheckOut;  // dd/MM/yyyy
    private String selectedRoomTitle;

    private static final DateTimeFormatter DMY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ----- Click book now -----
    @And("Click on the Book Now button.")
    public void click_on_book_now_button() {
        home = new HomePage(BaseTest.driver); // re-instantiate to ensure fresh state
        selectedRoomTitle = home.clickFirstBookNowAndCaptureRoomTitle();
        System.out.println("Remembered selected room: " + selectedRoomTitle);
        booking = new BookingPage(BaseTest.driver); // prepare for next steps
    }

    // ----- Landed on booking page and basic sections -----
    @And("User is navigated to the booking page showing the Room type selected")
    public void user_navigated_to_booking_page_showing_room_type() {
       assertTrue(booking.isLoaded(), "Booking page not loaded (no 'Book This Room' header).");

        // Room type presence (loose match e.g. "Single" or "Single Room")
        assertTrue(booking.pageContainsRoomTitle(selectedRoomTitle), "Booking page does not mention the selected room: " + selectedRoomTitle);

        System.out.println("✅ Booking page loaded and mentions room: " + selectedRoomTitle);
    }

    @And("The page also shows the {string} section")
    public void the_page_also_shows_section(String sectionName) {
        // sectionName expected: "Room Description"
        assertTrue(booking.isRoomDescriptionVisible(), " Room Description' is not visible.");
        System.out.println(sectionName + "' section is visible.");
    }

    @And("the booking page shows the header {string} and the price per night")
    public void booking_page_shows_header_and_price(String headerText) {
        // isLoaded() already asserts header; now ensure "per night" exists
        assertTrue(booking.isLoaded(),"Booking page header missing.");
        assertTrue(booking.isPerNightShown()," Price per night not shown.");
        System.out.println("Header '" + headerText + "' and 'per night' confirmed.");
    }

    /*
    // ----- Dates and price summary -----
    @And("The selected date matches the check-in and check-out date with offset {string} and nights {string}")
    public void selected_dates_match(String offsetDaysStr, String nightsStr) {
        int offset = Integer.parseInt(offsetDaysStr.trim());
        int nights = Integer.parseInt(nightsStr.trim());

        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        LocalDate checkIn = today.plusDays(offset);
        LocalDate checkOut = checkIn.plusDays(nights);

        lastCheckIn  = checkIn.format(DMY);
        lastCheckOut = checkOut.format(DMY);
        String checkInIso  = checkIn.format(ISO);
        String checkOutIso = checkOut.format(ISO);

        String pageDates = booking.grabAllVisibleDateText();
        System.out.println("Page date text: " + pageDates);

        boolean hasDMY = pageDates.contains(lastCheckIn) && pageDates.contains(lastCheckOut);
        boolean hasISO = pageDates.contains(checkInIso) && pageDates.contains(checkOutIso);

        assertTrue(hasDMY || hasISO,
                "Booking page does not show the expected dates (DMY or ISO).\n" +
                        "Expected DMY: " + lastCheckIn + " .. " + lastCheckOut + "\n" +
                        "Expected ISO: " + checkInIso + " .. " + checkOutIso + "\n" +
                        "Page text: " + pageDates
        );

        System.out.println("Dates confirmed on booking page (DMY or ISO).");
    }
    */

    @And("The selected date matches the check-in and check-out date with offset {string} and nights {string}")
    public void selected_dates_match(String offsetDaysStr, String nightsStr) {
        int offset = Integer.parseInt(offsetDaysStr.trim());
        int nights = Integer.parseInt(nightsStr.trim());

        LocalDate today    = LocalDate.now(ZoneId.systemDefault());
        LocalDate checkIn  = today.plusDays(offset);
        LocalDate checkOut = checkIn.plusDays(nights);

        String checkInDMY   = checkIn.format(DMY);           // dd/MM/yyyy
        String checkOutDMY  = checkOut.format(DMY);
        String checkInISO   = checkIn.format(ISO);           // yyyy-MM-dd
        String checkOutISO  = checkOut.format(ISO);

        // Primary check: URL query params
        String url     = booking.currentUrl();
        String ciQuery = booking.getCheckInFromUrl();
        String coQuery = booking.getCheckOutFromUrl();

        System.out.println("Booking URL: " + url);
        System.out.println("URL query → checkin=" + ciQuery + " | checkout=" + coQuery);
        System.out.println("Expected → ISO checkin=" + checkInISO + " | ISO checkout=" + checkOutISO);

        boolean urlMatches = checkInISO.equals(ciQuery) && checkOutISO.equals(coQuery);

        if (!urlMatches) {
            // 🔁 Fallback: try visible text scan (covers future UI changes)
            String pageDates = booking.grabAllVisibleDateText();
            System.out.println("🧾 Fallback page date text: " + pageDates);
            boolean hasDMY = pageDates.contains(checkInDMY) && pageDates.contains(checkOutDMY);
            boolean hasISO = pageDates.contains(checkInISO) && pageDates.contains(checkOutISO);

            assertTrue(hasDMY || hasISO, () ->
                    "Booking page does not show the expected dates.\n" +
                            "Expected (ISO): " + checkInISO + " .. " + checkOutISO + "\n" +
                            "Expected (DMY): " + checkInDMY + " .. " + checkOutDMY + "\n" +
                            "URL query     : checkin=" + ciQuery + " | checkout=" + coQuery + "\n" +
                            "Page text     : " + pageDates
            );
        } else {
            // URL matched; assert cleanly with JUnit 5 messages
            assertAll(
                    () -> assertEquals(checkInISO,  ciQuery,  "checkin param mismatch"),
                    () -> assertEquals(checkOutISO, coQuery, "checkout param mismatch")
            );
            System.out.println("Dates confirmed via URL params (ISO).");
        }
    }

    @And("The price summary section is shown")
    public void price_summary_section_is_shown() {
        assertTrue(booking.isPriceSummaryVisible(), "Price summary section is not visible.");
        System.out.println("Price summary is visible.");
    }

    @And("The calendar highlights the selected range for offset {string} and nights {string}")
    public void calendar_highlights_selected_range(String offsetDaysStr, String nightsStr) {
        int offset = Integer.parseInt(offsetDaysStr.trim());
        int nights = Integer.parseInt(nightsStr.trim());

        // Compute expected days (local tz)
        LocalDate today    = LocalDate.now(ZoneId.systemDefault());
        LocalDate checkIn  = today.plusDays(offset);
        LocalDate checkOut = checkIn.plusDays(nights);

        int checkInDayNum  = checkIn.getDayOfMonth();
        int checkOutDayNum = checkOut.getDayOfMonth();

        System.out.println("Expecting calendar selection: " + checkIn + " → " + checkOut +
                " (days " + checkInDayNum + " → " + checkOutDayNum + ")");

        // Make sure the calendar is on screen
        booking.scrollCalendarIntoView();

        // 1) At least one 'Selected' event marker exists (highlight bar)
        int markers = booking.countSelectedRangeMarkers();
        assertTrue(markers >= 1, () ->
                "No 'Selected' highlight marker found on the calendar.");

        // 2) The edge day numbers are visible in the month grid
        boolean startVisible = booking.isDayNumberVisibleInMonth(checkInDayNum);
        boolean endVisible   = booking.isDayNumberVisibleInMonth(checkOutDayNum);

        assertAll(
                () -> assertTrue(startVisible, "Check-in day button not visible in month grid: " + checkInDayNum),
                () -> assertTrue(endVisible,   "Check-out day button not visible in month grid: " + checkOutDayNum)
        );

        System.out.println("Calendar visually highlights the selected range (marker present; edge days visible).");
    }

    @And("User clicks on the Reserve Now button")
    public void user_clicks_on_the_reserve_now_button() {
        booking.scrollToReserveButton();
        booking.clickReserveNow();
    }

    @And("User sees booking form -  First Name, Last Name, Email and Phone number fields")
    public void user_sees_booking_form_fields() {
        boolean fieldsVisible = booking.areBookingFieldsVisible();
        assertTrue(fieldsVisible, "❌ Booking form fields are not visible.");
        System.out.println("✅ Booking form fields are visible.");
    }

    @And("User enters First Name, Last Name, Email and Phone Number")
    public void user_enters_first_last_email_phone() {
        // Use UK locale for names where possible
        Faker faker = new Faker(new Locale("en-GB"));

        String firstName = faker.name().firstName();
        String lastName  = faker.name().lastName();

        // Make a simple, unique email address
        String uniqueId  = String.valueOf(System.currentTimeMillis()).substring(8); // last few digits
        String emailAddr = (firstName + "." + lastName + uniqueId + "@example.test").toLowerCase();

        // UK mobile: 07 + 9 random digits = 11 total characters
        Random rnd = new Random();
        String nineDigits = String.format("%09d", rnd.nextInt(1_000_000_000));
        String phoneNumber = "07" + nineDigits; // e.g., 07xxxxxxxxx

        System.out.println("Generated data → first:" + firstName + ", last:" + lastName +
                ", email:" + emailAddr + ", phone:" + phoneNumber);

        // Simple guards to satisfy your rules
        assertTrue(phoneNumber.startsWith("07"), "Phone must start with 07.");
        assertEquals(11, phoneNumber.length(), "Phone must be 11 digits.");

        booking.fillBookingForm(firstName, lastName, emailAddr, phoneNumber);

        // Keep for potential later assertions
        this.lastCheckIn  = (this.lastCheckIn  == null) ? "" : this.lastCheckIn;
        this.lastCheckOut = (this.lastCheckOut == null) ? "" : this.lastCheckOut;
    }

    @And("Click on the Reserve Now button")
    public void click_on_the_reserve_now_button_to_submit() {
        booking.scrollToReserveButton();
        booking.clickReserveNow();
        // Optional: you can assert a success banner/URL change here when the app confirms a reservation
    }

    /*
    @And("Booking Confirmed is displayed, showing the selected check-in date and check-out date.")
    public void booking_confirmed_panel_shows_selected_dates() {
        // Ensure the panel is visible
        assertTrue(booking.isBookingConfirmedVisible(), "❌ Booking Confirmed panel not visible.");

        // Expected ISO dates from the URL query (?checkin=YYYY-MM-DD&checkout=YYYY-MM-DD)
        String expectedStartIso = booking.getCheckInFromUrl();
        String expectedEndIso   = booking.getCheckOutFromUrl();
        System.out.println("🧮 Expected (from URL): " + expectedStartIso + " → " + expectedEndIso);

        // Actual dates rendered on the panel (two <strong> tags)
        String actualStartIso = booking.getConfirmedStartIso();
        String actualEndIso   = booking.getConfirmedEndIso();
        System.out.println("🧾 Confirmed dates shown: " + actualStartIso + " → " + actualEndIso);

        assertAll(
                () -> assertEquals(expectedStartIso, actualStartIso, "Check-in date on the confirmation card is wrong."),
                () -> assertEquals(expectedEndIso,   actualEndIso,   "Check-out date on the confirmation card is wrong.")
        );

        System.out.println("✅ Booking Confirmed dates match the selected range.");
    }
    */

    @And("Booking Confirmed is displayed, showing the selected check-in date and check-out date.")
    public void booking_confirmed_panel_shows_selected_dates() {
        assertTrue(booking.isBookingConfirmedVisible(), "Booking Confirmed panel not visible.");

        String expectedStartIso = booking.getCheckInFromUrl();
        String expectedEndIso   = booking.getCheckOutFromUrl();
        System.out.println("🧮 Expected (from URL): " + expectedStartIso + " → " + expectedEndIso);

        String[] actual = booking.getConfirmedDatesIso();
        assertTrue(actual.length >= 2 && !actual[0].isEmpty() && !actual[1].isEmpty(),
                "Could not read two ISO dates from confirmation card.");

        String actualStartIso = actual[0];
        String actualEndIso   = actual[1];
        System.out.println("🧾 Confirmed dates shown: " + actualStartIso + " → " + actualEndIso);

        assertAll(
                () -> assertEquals(expectedStartIso, actualStartIso, "Check-in date on the confirmation card is wrong."),
                () -> assertEquals(expectedEndIso,   actualEndIso,   "Check-out date on the confirmation card is wrong.")
        );
        System.out.println("✅ Booking Confirmed dates match the selected range.");
    }


    @And("the Return Home page button is shown.")
    public void return_home_button_is_shown() {
        assertTrue(booking.isReturnHomeShown(), "'Return home' button is not visible.");
        System.out.println("✅ 'Return home' button is visible.");
    }

    @And("User click on Return home button")
    public void user_click_on_return_home() {
        booking.clickReturnHome();
    }

    @When("User clicks on the Reserve Now button without entering any data")
    public void user_clicks_reserve_now_with_empty_form() {
        System.out.println("🔎 Submitting empty booking form to trigger validation...");
        booking.clickReserveNow();
    }

    @Then("User sees validation messages for First Name, Last Name, Email and Phone Number fields")
    public void user_sees_validation_messages(DataTable expectedTable) {
        List<String> expectedPhrases = expectedTable.asList().stream()
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();

        List<String> actualMessages = booking.getValidationMessages();
        String actualBlob = String.join(" | ", actualMessages).toLowerCase();

        // Build Executables (not Runnables)
        Executable[] checks = expectedPhrases.stream()
                .map(exp -> (Executable) () ->
                        assertTrue(
                                actualBlob.contains(exp.toLowerCase()),
                                () -> "\nExpected phrase not found: \"" + exp + "\"\n" +
                                        "Actual messages: " + actualMessages + "\n"
                        )
                )
                .toArray(Executable[]::new);

        // Varargs overload
        assertAll("All expected validation phrases should appear somewhere on the page", checks);

        System.out.println("All expected validation phrases were present.");
    }

    @And("Click on Cancel Booking button")
    public void click_on_cancel_booking_button() {
        booking.clickCancelBooking();
    }
}

