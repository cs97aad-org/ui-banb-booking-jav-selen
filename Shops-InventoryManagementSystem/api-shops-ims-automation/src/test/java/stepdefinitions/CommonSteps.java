package stepdefinitions;

import io.cucumber.java.en.Then;
import io.restassured.response.Response;

import static org.junit.jupiter.api.Assertions.*;

public class CommonSteps {
    private final SharedContext context;

    public CommonSteps(SharedContext context) {
        this.context = context;
    }

    @Then("the response status should be {int}")
    public void validate_status_code(int expectedStatus) {
        Response response = context.getLatestResponse();
        assertNotNull(response, "No response available for status check");
        response.then().statusCode(expectedStatus);
    }

    @Then("the error message should be {string}")
    public void the_error_message_should_be(String expectedMessage) {
        Response response = context.getLatestResponse();
        assertNotNull(response, "No response available for message validation");
        String actualMessage = response.jsonPath().getString("message");
        assertEquals(expectedMessage.trim(), actualMessage.trim());
    }

    @Then("the response should contain a non-empty token")
    public void validate_token_present() {
        String token = context.getLatestResponse().jsonPath().getString("token");
        assertNotNull(token, "Token should not be null");
        assertFalse(token.isEmpty(), "Token should not be empty");
    }

    @Then("the response should contain a valid product ID")
    public void validate_product_id_present() {
        String productId = context.getLatestResponse().jsonPath().getString("productId");
        assertNotNull(productId, "Product ID should not be null");
        assertFalse(productId.isEmpty(), "Product ID should not be empty");
    }

    @Then("the response should contain stock summary with {int} buys, {int} sells, and {int} in stock")
    public void validate_stock_summary_fields(int expectedBuys, int expectedSells, int expectedStock) {
        Response response = context.getLatestResponse();
        assertEquals(expectedBuys, response.jsonPath().getInt("totalBuys"));
        assertEquals(expectedSells, response.jsonPath().getInt("totalSells"));
        assertEquals(expectedStock, response.jsonPath().getInt("currentStock"));
        assertEquals(2, response.jsonPath().getInt("totalTransactions"));
    }
}
