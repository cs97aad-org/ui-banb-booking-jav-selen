package utils;

import dto.ProductRequest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class TestDataGenerator {
    private static final Random random = new Random();
    private static int productCounter = 1;

    private static final String[] PRODUCT_TYPES = {
            "games", "computer accessory", "laptops", "miscellaneous", "mobile"
    };

    public static String generateProductName() {
        String timestamp = new SimpleDateFormat("ddMMyy_HHmmss").format(new Date());
        String counter = String.format("%04d", productCounter++);
        return "product" + counter + "_" + timestamp;
    }

    public static double generateRandomPrice() {
        return Math.round((0.99 + (99.0 * random.nextDouble())) * 100.0) / 100.0;
    }

    public static int generateRandomQuantity() {
        return random.nextInt(121); // 0–120
    }

    public static String generateRandomProductType() {
        return PRODUCT_TYPES[random.nextInt(PRODUCT_TYPES.length)];
    }

    public static ProductRequest generateRandomProductRequest() {
        return new ProductRequest(
                generateProductName(),
                generateRandomPrice(),
                generateRandomProductType(),
                generateRandomQuantity()
        );
    }

    public static String generateUpdatedProductName() {
        return generateProductName() + "U";
    }
}
