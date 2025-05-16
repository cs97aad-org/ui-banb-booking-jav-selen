package stepdefinitions;

import dto.ProductRequest;
import io.cucumber.java.en.*;
import io.restassured.response.Response;
import utils.APIHelper;
import utils.TokenManager;
import utils.TestDataGenerator;

import static org.junit.jupiter.api.Assertions.*;

public class ProductSteps {

    private ProductRequest product;
    private String createdProductId; // To store ID after creation
    private ProductRequest updatedProduct;
    private final SharedContext context;

    public ProductSteps(SharedContext context) {
        this.context = context;
    }

    @Given("I am an authenticated user")
    public void i_am_authenticated_user() {
        TokenManager.getToken(); // Token will be cached
    }

    @Given("I generate a random product")
    public void generate_random_product() {
        product = TestDataGenerator.generateRandomProductRequest();
        System.out.println("Generated Product: " +
                "name=" + product.getName() + ", " +
                "price=" + product.getPrice() + ", " +
                "type=" + product.getProductType() + ", " +
                "quantity=" + product.getQuantity());
    }

    @When("I send the create product request")
    public void send_create_product_request() {
        Response response = APIHelper.postWithAuth("/products", product);
        context.setLatestResponse(response);
        String productId = response.jsonPath().getString("productId");
        System.out.println("productId generated from create product " + productId);
    }

    @When("I create a product with name {string}, price {double}, type {string}, and quantity {int}")
    public void i_create_a_product(String name, double price, String type, int quantity) {
        product = new ProductRequest(name, price, type, quantity);
        Response response = APIHelper.postWithAuth("/products", product);
        context.setLatestResponse(response);
    }

    @And("the response should include the created product details")
    public void validate_created_product_response() {
        Response response = context.getLatestResponse();
        String productId = response.jsonPath().getString("productId");
        String name = response.jsonPath().getString("name");
        double price = response.jsonPath().getDouble("price");
        String productType = response.jsonPath().getString("productType");
        int quantity = response.jsonPath().getInt("quantity");
        String createdAt = response.jsonPath().getString("createdAt");

        assertNotNull(productId, "Product ID should not be null");
        assertEquals(product.getName(), name, "Product name mismatch");
        assertEquals(product.getPrice(), price, 0.01, "Price mismatch");
        assertEquals(product.getProductType(), productType, "Product type mismatch");
        assertEquals(product.getQuantity(), quantity, "Quantity mismatch");
        assertNotNull(createdAt, "CreatedAt timestamp should not be null");
    }

    @When("I send the create product request without authentication")
    public void iSendTheCreateProductRequestWithoutAuthentication() {
        Response response = APIHelper.postWithOutAuth("/products", product);
        context.setLatestResponse(response);
    }


    /******************* Getting products*************************/

    @Given("a product has been created")
    public void product_has_been_created() {
        product = TestDataGenerator.generateRandomProductRequest();
        Response response = APIHelper.postWithAuth("/products", product);
        createdProductId = response.jsonPath().getString("productId");
        response.then().statusCode(201);
        context.setLatestResponse(response);
        System.out.println("Generated productId: " + createdProductId);
    }

    @When("I retrieve the product by ID")
    public void retrieve_product_by_id() {
        System.out.println(" Generated product Id: " + createdProductId);
        Response response = APIHelper.getWithAuth("/products/" + createdProductId);
        context.setLatestResponse(response);
    }

    @When("I retrieve a product using an invalid ID")
    public void retrieve_invalid_product() {
        Response response = APIHelper.getWithAuth("/products/1234567");
        context.setLatestResponse(response);
    }

    @When("I retrieve the product by ID without authentication")
    public void retrieve_product_without_auth() {
        Response response = APIHelper.getWithOutAuth("/products/", "44bd694e-8c23-4350-91f5-f1c5f1ca5343");
        context.setLatestResponse(response);
    }

    @Then("the retrieved product details should match the created product")
    public void match_retrieved_to_created() {
        Response response = context.getLatestResponse();
        assertEquals(product.getName(), response.jsonPath().getString("name"));
        assertEquals(product.getPrice(), response.jsonPath().getDouble("price"), 0.01);
        assertEquals(product.getProductType(), response.jsonPath().getString("productType"));
        assertEquals(product.getQuantity(), response.jsonPath().getInt("quantity"));
    }

    @When("I retrieve all products")
    public void i_retrieve_all_products() {
        Response response = APIHelper.getWithAuth("/products");
        context.setLatestResponse(response);
    }

    @When("I retrieve all products without authentication")
    public void i_retrieve_all_products_without_authentication() {
        Response response = APIHelper.getAllWithOutAuth("/products");
        context.setLatestResponse(response);
    }

    @And("the created product should be listed in the response array")
    public void created_product_should_be_listed() {
        Response response = context.getLatestResponse();
        boolean found = response.jsonPath().getList("productId").contains(createdProductId);
        assertTrue(found, "Created product ID should be in the response array");
    }

    /************ Updating product *********************************************/

    @Given("I prepare valid product update data")
    public void prepare_valid_update_data() {
        updatedProduct = new ProductRequest(
                TestDataGenerator.generateUpdatedProductName(),
                TestDataGenerator.generateRandomPrice(),
                "laptops",
                TestDataGenerator.generateRandomQuantity()
        );
    }

    @When("I update the product with a new name, price and quantity")
    public void i_update_the_product() {
        updatedProduct = new ProductRequest(
                TestDataGenerator.generateUpdatedProductName(),
                TestDataGenerator.generateRandomPrice(),
                product.getProductType(),
                TestDataGenerator.generateRandomQuantity()
        );

        Response response = APIHelper.putWithAuth("/products/" + createdProductId, updatedProduct);
        context.setLatestResponse(response);
    }

    @Then("the response should reflect the updated product details")
    public void verify_updated_product_response() {
        /* validate the updated values against the response */
        Response response = context.getLatestResponse();
        assertEquals(updatedProduct.getName(), response.jsonPath().getString("name"));
        assertEquals(updatedProduct.getPrice(), response.jsonPath().getDouble("price"), 0.01);
        assertEquals(updatedProduct.getProductType(), response.jsonPath().getString("productType"));
        assertEquals(updatedProduct.getQuantity(), response.jsonPath().getInt("quantity"));
        assertEquals(createdProductId, response.jsonPath().getString("productId"));
        assertNotNull(response.jsonPath().getString("createdAt"));
    }


    @When("I update a product using an invalid ID")
    public void update_with_invalid_id() {
        Response response = APIHelper.putWithAuth("/products/1234567", updatedProduct);
        context.setLatestResponse(response);
    }

    @When("I update the product without authentication")
    public void update_without_authentication() {
        ProductRequest updatedProduct;
        updatedProduct = new ProductRequest(
                TestDataGenerator.generateUpdatedProductName(),
                TestDataGenerator.generateRandomPrice(),
                "mobile",
                TestDataGenerator.generateRandomQuantity()
        );
        System.out.println("Generated updated Product: " +
                "name=" + updatedProduct.getName() + ", " +
                "price=" + updatedProduct.getPrice() + ", " +
                "type=" + updatedProduct.getProductType() + ", " +
                "quantity=" + updatedProduct.getQuantity());

        Response response = APIHelper.updateProductWithOutAuth("/products/44bd694e-8c23-4350-91f5-f1c5f1ca5343", updatedProduct);
        context.setLatestResponse(response);
    }


    /**************  Deleting Product ****************************************************/

    @When("I delete the product by ID")
    public void delete_product_by_id() {
        Response response = APIHelper.deleteWithAuth("/products/" + createdProductId);
        context.setLatestResponse(response);
    }

    @When("I delete a product using an invalid ID")
    public void delete_invalid_product() {
        Response response = APIHelper.deleteWithAuth("/products/1234567");
        context.setLatestResponse(response);
    }

    @When("I delete the product without authentication")
    public void delete_without_authentication() {
        Response response = APIHelper.deleteProductWithOutAuth("/products/", "44bd694e-8c23-4350-91f5-f1c5f1ca5343");
        context.setLatestResponse(response);
    }

    @And("the delete response message should be {string}")
    public void validate_delete_message(String expectedMessage) {
        Response response = context.getLatestResponse();
        String actual = response.jsonPath().getString("message");
        assertEquals(expectedMessage, actual);
    }

}