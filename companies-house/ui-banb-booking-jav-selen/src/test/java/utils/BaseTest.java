package utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

import java.net.URL;
import java.time.Duration;
import java.util.Locale;

public class BaseTest {
    public static WebDriver driver;
    public static WaitUtils wait;

    // timeouts & base url from your existing ConfigLoader
    private static final String BASE_URL = ConfigLoader.get().baseUrl();
    private static final int IMPLICIT_WAIT_SEC = ConfigLoader.get().implicitWaitSeconds();
    private static final int PAGELOAD_TIMEOUT_SEC = ConfigLoader.get().pageLoadTimeoutSeconds();

    // read once from JVM/ENV
    private static final String BROWSER =
            firstNonBlank(System.getProperty("browser"), System.getenv("BROWSER"), "chrome")
                    .toLowerCase(Locale.ROOT);
    private static final boolean HEADLESS =
            Boolean.parseBoolean(firstNonBlank(System.getProperty("headless"),
                    System.getenv("HEADLESS"), "false"));
    private static final String REMOTE_URL =
            firstNonBlank(System.getProperty("remoteUrl"), System.getenv("REMOTE_URL"), "");

    public static void setup() {
        if (driver != null) return;

        System.out.println("🔧 Starting WebDriver -> browser=" + BROWSER +
                ", headless=" + HEADLESS + (REMOTE_URL.isBlank() ? "" : ", remote=" + REMOTE_URL));

        switch (BROWSER) {
            case "firefox" -> driver = createFirefox();
            case "edge"    -> driver = createEdge();
            case "safari"  -> driver = createSafari();
            default        -> driver = createChrome();
        }

        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(IMPLICIT_WAIT_SEC));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(PAGELOAD_TIMEOUT_SEC));
        wait = new WaitUtils(driver);

        Runtime.getRuntime().addShutdownHook(new Thread(BaseTest::tearDown));
        System.out.println("WebDriver ready.");
    }

    public static void openHome() {
        if (driver == null) setup();
        System.out.println("Opening: " + BASE_URL);
        driver.get(BASE_URL);
    }

    public static void tearDown() {
        System.out.println("Closing browser...");
        if (driver != null) {
            try { driver.quit(); } finally { driver = null; }
        }
    }

    // ---------- per-browser creators ----------

    private static WebDriver createChrome() {
        ChromeOptions opts = new ChromeOptions();
        if (HEADLESS) opts.addArguments("--headless=new", "--window-size=1920,1080");
        opts.addArguments("--no-sandbox", "--disable-dev-shm-usage");
        if (!REMOTE_URL.isBlank()) return remote(opts);
        WebDriverManager.chromedriver().setup();
        return new ChromeDriver(opts);
    }

    private static WebDriver createFirefox() {
        FirefoxOptions opts = new FirefoxOptions();
        if (HEADLESS) opts.addArguments("-headless");
        if (!REMOTE_URL.isBlank()) return remote(opts);
        WebDriverManager.firefoxdriver().setup();
        return new FirefoxDriver(opts);
    }

    private static WebDriver createEdge() {
        EdgeOptions opts = new EdgeOptions();
        if (HEADLESS) opts.addArguments("--headless=new", "--window-size=1920,1080");
        if (!REMOTE_URL.isBlank()) return remote(opts);
        WebDriverManager.edgedriver().setup();
        return new EdgeDriver(opts);
    }

    private static WebDriver createSafari() {
        // Safari doesn't support headless. Ensure:
        // 1) macOS only  2) Safari > Preferences > Advanced > "Show Develop menu"
        // 3) In Terminal: `safaridriver --enable` (once)
        if (HEADLESS) System.out.println("⚠️ Safari headless not supported; ignoring.");
        SafariOptions opts = new SafariOptions();
        if (!REMOTE_URL.isBlank()) return remote(opts); // works only with a macOS Safari node
        return new SafariDriver(opts);
    }

    // ---------- remote helper ----------

    private static WebDriver remote(org.openqa.selenium.Capabilities options) {
        try {
            return new RemoteWebDriver(new URL(REMOTE_URL), options);
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to remote WebDriver: " + REMOTE_URL, e);
        }
    }

    private static String firstNonBlank(String... v) {
        for (String s : v) if (s != null && !s.isBlank()) return s.trim();
        return "";
    }
}
