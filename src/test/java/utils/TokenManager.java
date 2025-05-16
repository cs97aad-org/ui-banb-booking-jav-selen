package utils;

import config.ConfigManager;
import dto.LoginRequest;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class TokenManager {
    private static String token;

    public static String getToken() {
        if (token == null) {
            LoginRequest login = new LoginRequest(ConfigManager.DEFAULT_USERNAME, ConfigManager.DEFAULT_PASSWORD);
            Response response = given()
                    .log().all()
                    .header("Content-Type", "application/json")
                    .body(login)
                    .post(ConfigManager.BASE_URL + ConfigManager.LOGIN_ENDPOINT);

            response.then().statusCode(200);
            token = response.jsonPath().getString("token");
        }
        return token;
    }
}