package config;

import io.restassured.RestAssured;

public class ConfigManager {
    public static final String BASE_URL = "https://apiforshopsinventorymanagementsystem.onrender.com";
    public static final String LOGIN_ENDPOINT = "/auth/login";
    public static final String DEFAULT_USERNAME = "user01";
    public static final String DEFAULT_PASSWORD = "secpassword*";

    static {
        RestAssured.baseURI = BASE_URL;  //Globally sets the base URL once
    }
}