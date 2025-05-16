package stepdefinitions;

import config.ConfigManager;
import dto.LoginRequest;
import io.cucumber.java.en.*;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class AuthSteps {

    private LoginRequest loginPayload;
    private final SharedContext context;

    public AuthSteps(SharedContext context) {
        this.context = context;
    }

    @Given("I have a valid username and password")
    public void i_have_a_valid_username_and_password() {
        loginPayload = new LoginRequest(ConfigManager.DEFAULT_USERNAME, ConfigManager.DEFAULT_PASSWORD);
    }

    @When("I send a login request")
    public void i_send_a_login_request() {

        System.out.println("Sending login request to: " + ConfigManager.BASE_URL + ConfigManager.LOGIN_ENDPOINT);
        System.out.println("Payload: username = " + loginPayload.getUsername() + ", password = " + loginPayload.getPassword());

        Response response = given()
                .log().all()
                .header("Content-Type", "application/json")
                .body(loginPayload)
                .post(ConfigManager.BASE_URL + ConfigManager.LOGIN_ENDPOINT)
                .prettyPeek();
        context.setLatestResponse(response);
        // Print full response
        System.out.println("Response Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody().asPrettyString());

    }

    @Then("I should receive a token")
    public void i_should_receive_a_token() {
        Response response = context.getLatestResponse();
        response.then().statusCode(200);
        String token = response.jsonPath().getString("token");
        assertNotNull(token, "Token should not be null");
    }

    @Given("I have invalid login details")
    public void i_have_invalid_login_details() {
        loginPayload = new LoginRequest("invalidUser", "wrongPassword");
    }

    @Given("I have a malformed login payload")
    public void i_have_a_malformed_login_payload() {
        // Simulate malformed payload using incorrect JSON (extra field, missing required field)
        String badJson = "{\"username\":\"user01\", \"passwords\":\"wrongFieldName\"}";
        Response response = given()
                .log().all()
                .header("Content-Type", "application/json")
                .body(badJson)
                .post(ConfigManager.BASE_URL + ConfigManager.LOGIN_ENDPOINT)
                .prettyPeek();
        context.setLatestResponse(response);
    }


    @Then("I should receive a {int} status code with message {string}")
    public void i_should_receive_status_code_with_message(int statusCode, String message) {
        Response response = context.getLatestResponse();
        response.then().statusCode(statusCode);
        assertEquals(message, response.jsonPath().getString("message"));
    }
}