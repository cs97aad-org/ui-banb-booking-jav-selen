package utils;

import java.io.InputStream;
import java.util.Properties;

public final class ConfigLoader {

    private static ConfigLoader INSTANCE;
    private final Properties props = new Properties();

    private ConfigLoader() {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (in != null) props.load(in);
        } catch (Exception ignored) {}
    }

    public static ConfigLoader get() {
        if (INSTANCE == null) INSTANCE = new ConfigLoader();
        return INSTANCE;
    }

    public String baseUrl() {
        return firstNonBlank(
                System.getProperty("baseUrl"),
                System.getenv("BASE_URL"),
                props.getProperty("baseUrl"),
                "https://automationintesting.online"
        );
    }

    public int implicitWaitSeconds() {
        return Integer.parseInt(firstNonBlank(
                System.getProperty("implicitWait"),
                System.getenv("IMPLICIT_WAIT"),
                props.getProperty("implicitWait"),
                "10"
        ));
    }

    public int pageLoadTimeoutSeconds() {
        return Integer.parseInt(firstNonBlank(
                System.getProperty("pageLoadTimeout"),
                System.getenv("PAGE_LOAD_TIMEOUT"),
                props.getProperty("pageLoadTimeout"),
                "30"
        ));
    }

    private static String firstNonBlank(String... values) {
        for (String v : values) if (v != null && !v.isBlank()) return v.trim();
        return "";
    }
}
