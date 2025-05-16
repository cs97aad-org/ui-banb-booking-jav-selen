package stepdefinitions;

import dto.OrderRequest;
import dto.ProductRequest;
import io.cucumber.java.en.*;
import io.restassured.response.Response;
import utils.APIHelper;
import utils.TestDataGenerator;


import static org.junit.jupiter.api.Assertions.*;

public class OrderSteps {

    private ProductRequest product;
    private OrderRequest orderRequest;
    private String productId;
    private final SharedContext context;

    public OrderSteps(SharedContext context) {
        this.context = context;
    }

    @Given("a new product is created")
    public void create_product_for_order() {
        product = TestDataGenerator.generateRandomProductRequest();
        Response response = APIHelper.postWithAuth("/products", product);
        productId = response.jsonPath().getString("productId");
        response.then().statusCode(201);
        context.setLatestResponse(response);
    }

    @When("I place a buy order with quantity {int}")
    public void place_valid_buy_order(int quantity) {
        orderRequest = new OrderRequest("buy", productId, quantity);
        Response response = APIHelper.postWithAuth("/orders", orderRequest);
        context.setLatestResponse(response);
    }

    @When("I place a buy order with invalid order type {string}")
    public void place_buy_order_invalid_type(String orderType) {
        orderRequest = new OrderRequest(orderType, productId, 1);
        Response response = APIHelper.postWithAuth("/orders", orderRequest);
        context.setLatestResponse(response);
    }

    @When("I place a buy order for invalid productId {string}")
    public void place_buy_order_invalid_product(String invalidProductId) {
        orderRequest = new OrderRequest("buy", invalidProductId, 27);
        Response response = APIHelper.postWithAuth("/orders", orderRequest);
        context.setLatestResponse(response);
    }

    @When("I place a buy order without authentication")
    public void place_buy_order_no_token() {
        orderRequest = new OrderRequest("buy", productId, 1);
        Response response = APIHelper.orderWithOutAuth("/orders", orderRequest);
        context.setLatestResponse(response);
    }

    @Then("the buy order response should include the updated stock")
    public void validate_buy_response() {
        Response response = context.getLatestResponse();
        assertEquals(true, response.jsonPath().getBoolean("success"));
        assertEquals(productId, response.jsonPath().getString("productId"));
        assertEquals("buy", response.jsonPath().getString("orderType"));
        assertEquals(1, response.jsonPath().getInt("quantity"));
        assertNotNull(response.jsonPath().getString("orderId"));
        assertNotNull(response.jsonPath().getInt("newStock"));
    }

    @Given("a product is created and stocked with quantity {int}")
    public void create_and_stock_product(int quantity) {
        product = TestDataGenerator.generateRandomProductRequest();
        Response response = APIHelper.postWithAuth("/products", product);
        productId = response.jsonPath().getString("productId");
        response.then().statusCode(201);

        // Place a buy order to stock it
        OrderRequest buyOrder = new OrderRequest("buy", productId, quantity);
        Response response1 = APIHelper.postWithAuth("/orders", buyOrder);
        context.setLatestResponse(response1);
        response.then().statusCode(201);
    }

    @When("I place a sell order with quantity {int}")
    public void place_sell_order(int quantity) {
        orderRequest = new OrderRequest("sell", productId, quantity);
        Response response = APIHelper.postWithAuth("/orders", orderRequest);
        context.setLatestResponse(response);
    }

    @Then("the sell order response should include the updated stock")
    public void validate_sell_response() {
        Response response;
        response = context.getLatestResponse();
        assertEquals(true, response.jsonPath().getBoolean("success"));
        assertEquals(productId, response.jsonPath().getString("productId"));
        assertEquals("sell", response.jsonPath().getString("orderType"));
        assertNotNull(response.jsonPath().getString("orderId"));
        int newStock = response.jsonPath().getInt("newStock");
        int expectedStock = response.jsonPath().getInt("previousStock") - response.jsonPath().getInt("quantity");
        assertEquals(expectedStock, newStock);
    }

    @When("I retrieve the stock level for that product")
    public void get_stock_level() {
        //   stockResponse = APIHelper.getWithAuth("/orders/product/" + productId);
        Response response = APIHelper.getWithAuth("/orders/product/" + productId);
        context.setLatestResponse(response);
    }

    @Then("the stock level response should show {int} buys, {int} sells and {int} in stock")
    public void validate_stock_summary(int totalBuys, int totalSells, int currentStock) {
        Response response = context.getLatestResponse();
        assertEquals(productId, response.jsonPath().getString("productId"));
        assertEquals(totalBuys, response.jsonPath().getInt("totalBuys"));
        assertEquals(totalSells, response.jsonPath().getInt("totalSells"));
        assertEquals(currentStock, response.jsonPath().getInt("currentStock"));
        assertEquals(2, response.jsonPath().getInt("totalTransactions")); // 2 is the total POST request made using sell and buy orderType
    }

    @Given("a new product is created without any transactions")
    public void product_without_orders() {
        product = TestDataGenerator.generateRandomProductRequest();
        Response response = APIHelper.postWithAuth("/products", product);
        response.then().statusCode(201);
        productId = response.jsonPath().getString("productId");
        context.setLatestResponse(response);
    }

    @When("I retrieve the stock level for that product without authentication")
    public void get_stock_without_auth() {
        Response response = APIHelper.getStockWithOutAuth("/orders/product/", "e846079f-7d3e-440e-af30-5b6b59b4d7ae");
        context.setLatestResponse(response);
    }
}
