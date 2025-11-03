package stepdefinitions;

import io.cucumber.java.en.*;
import net.datafaker.Faker;
import org.openqa.selenium.WebDriver;
import pages.ContactPage;
import pages.HomePage;
import utils.BaseTest;

import java.util.Locale;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class ContactSteps extends BaseTest {

    private HomePage homePage;
    private ContactPage contactPage;

    // Keep generated values for the final assertion
    private String senderFullName;
    private String emailAddress;
    private String mobileNumber;
    private String subjectText;
    private String messageBody;


    @When("User clicks on the Contact link on the navbar")
    public void user_clicks_on_contact_link() {
        homePage = new HomePage(BaseTest.driver);  // Initialize HomePage
        homePage.clickContactNav();
        contactPage = new ContactPage(BaseTest.driver); // Initialize ContactPage
        System.out.println("Clicked on Contact link in navbar.");
    }

    @Then("User is navigated to the Send Us a Message section")
    public void user_is_navigated_to_contact_section() {
        contactPage.waitForContactSection();
        System.out.println("Reached 'Send Us a Message' section.");
    }

    @And("User sees the Name, Email, Phone, Subject and Message fields")
    public void user_sees_contact_fields() {
        assertTrue(contactPage.areAllFieldsVisible(), "One or more Contact fields are not visible.");
        System.out.println("All Contact fields are visible.");
    }

    @When("User completes the Name, Email, Phone, Subject and Message fields")
    public void user_completes_contact_form() {
        Faker faker = new Faker(new Locale("en-GB"));
        String firstName  = faker.name().firstName();
        String lastName   = faker.name().lastName();
        senderFullName    = firstName + " " + lastName;

        String unique     = String.valueOf(System.currentTimeMillis()).substring(8);
        emailAddress      = (firstName + "." + lastName + unique + "@example.test").toLowerCase();

        // UK mobile: start 07 + 9 digits = 11 total
        String nineDigits = String.format("%09d", new Random().nextInt(1_000_000_000));
        mobileNumber      = "07" + nineDigits;

        subjectText       = "Subject " + faker.number().digits(4);

        // Message must be >= 20 chars
        messageBody       = faker.lorem().sentence(8);
        if (messageBody.length() < 20) {
            messageBody = messageBody + " " + faker.lorem().sentence(8);
        }
        assertTrue(messageBody.length() >= 20, "❌ Message should be at least 20 characters.");

        // Some quick format checks
        assertTrue(mobileNumber.startsWith("07"), "❌ Phone must start with 07.");
        assertEquals(11, mobileNumber.length(), "❌ Phone must be 11 digits.");

        System.out.println("🧪 Contact data → name:" + senderFullName + ", email:" + emailAddress +
                ", phone:" + mobileNumber + ", subject:" + subjectText + ", msgLen:" + messageBody.length());

        contactPage.fillContactForm(senderFullName, emailAddress, mobileNumber, subjectText, messageBody);
    }

    @And("User clicks the Submit button")
    public void user_clicks_submit() {
        contactPage.clickSubmit();
    }

    @Then("User is shown a message {string} with the senders Name and Subject entered")
    public void user_is_shown_confirmation(String expectedPhrase) {
        String headingText = contactPage.getConfirmationHeading();   // e.g., "Thanks for getting in touch Name1!"
        String subjectShown = contactPage.getConfirmationSubject();   // e.g., "Subject111"

        System.out.println("🧾 Confirmation heading: " + headingText);
        System.out.println("🧾 Confirmation subject: " + subjectShown);

        assertAll(
                () -> assertTrue(headingText.startsWith(expectedPhrase),
                        "Heading does not start with expected phrase.\nExpected starts with: " + expectedPhrase + "\nActual: " + headingText),
                // Name is interpolated into the heading; just ensure our first name appears in it
                () -> assertTrue(headingText.contains(senderFullName.split(" ")[0]),
                        "Sender's first name not present in heading. Expected part of: " + senderFullName),
                () -> assertEquals(subjectText, subjectShown, "Subject shown on confirmation does not match.")
        );

        System.out.println("✅ Confirmation shows expected phrase, sender name and subject.");
    }
}
