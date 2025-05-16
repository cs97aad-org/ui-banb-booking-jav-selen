package utils;

import config.ConfigManager;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class APIHelper {

    private static final String INVALID_TOKEN = "IeyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjY4MTJiY2NlZDVkZGIzYjkyYmY5MTYxMSIsImlhdCI6MTc0NzI0NTU4NCwiZXhwIjoxNzQ3MjQ5MTg0fQ.hTeFo8zUQZjqF2VsbRJanCNsdZQlmShhz3o_AcdkARQ";

    public static Response postWithAuth(String endpoint, Object body) {
        return given()
                .log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + TokenManager.getToken())
                .body(body)
                .post(ConfigManager.BASE_URL + endpoint)
                .prettyPeek();
    }

    public static Response getWithAuth(String endpoint) {
        return given()
                .log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + TokenManager.getToken())
                .get(ConfigManager.BASE_URL + endpoint)
                .prettyPeek();
    }

    public static Response putWithAuth(String endpoint, Object body) {
        return given()
                .log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + TokenManager.getToken())
                .body(body)
                .put(ConfigManager.BASE_URL + endpoint)
                .prettyPeek();
    }

    public static Response deleteWithAuth(String endpoint) {
        return given()
                .log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + TokenManager.getToken())
                .delete(ConfigManager.BASE_URL + endpoint)
                .prettyPeek();
    }

    public static Response postWithOutAuth(String endpoint, Object body) {
        String fullUrl = ConfigManager.BASE_URL + endpoint;
        System.out.println("Post request to: " + fullUrl);

        return given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(body)
                .post(fullUrl)
                .prettyPeek();
    }

    public static Response getWithOutAuth(String endpoint, String productId) {
        String fullUrl = ConfigManager.BASE_URL + endpoint + productId;
        System.out.println("GET request to: " + fullUrl);

        return given()
                .log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + INVALID_TOKEN)
                .get(fullUrl)
                .prettyPeek();  // prints full response to console
    }

    public static Response getAllWithOutAuth(String endpoint) {
        String fullUrl = ConfigManager.BASE_URL + endpoint;
        System.out.println("GET All Products Request URL: " + fullUrl);

        return given()
                .log().all()
                .contentType(ContentType.JSON)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + INVALID_TOKEN)
                .get(fullUrl)
                .prettyPeek();
    }

    public static Response updateProductWithOutAuth(String endpoint, Object body) {
        String fullUrl = ConfigManager.BASE_URL + endpoint;
        System.out.println("PUT request to: " + fullUrl);

        return given()
                .log().all()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + INVALID_TOKEN)
                .body(body)
                .put(fullUrl)
                .prettyPeek();
    }


    public static Response deleteProductWithOutAuth(String endpoint, String productId) {
        String fullUrl = ConfigManager.BASE_URL + endpoint + productId;
        System.out.println("Delete request to: " + fullUrl);

        return given()
                .log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + INVALID_TOKEN)
                .delete(fullUrl)
                .prettyPeek();  // prints full response to console
    }

    public static Response orderWithOutAuth(String endpoint, Object body) {
        String fullUrl = ConfigManager.BASE_URL + endpoint;
        System.out.println("Post Order URL to: " + fullUrl);

        return io.restassured.RestAssured
                .given().log().all()
                .contentType("application/json")
                .header("Authorization", "Bearer " + INVALID_TOKEN)
                .body(body)
                .post(ConfigManager.BASE_URL + endpoint)
                .prettyPeek();
    }


    public static Response getStockWithOutAuth(String endpoint, String productId) {
        String fullUrl = ConfigManager.BASE_URL + endpoint + productId;
        System.out.println("GET Stock Order URL request to: " + fullUrl);

        return given()
                .log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + INVALID_TOKEN)
                .get(fullUrl)
                .prettyPeek();
    }

}