
import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Created by naco_siren on 4/15/17.
 */


public class SeleniumTemp {
    public static void main(String... args) {
        System.setProperty("webdriver.gecko.driver", "lib\\geckodriver.exe");

        /* Selenium capacities */
        DesiredCapabilities capabilities = DesiredCapabilities.firefox();
        capabilities.setCapability("marionette", true);
        capabilities.setBrowserName("firefox");
        capabilities.setVersion("");
        capabilities.setPlatform(Platform.MAC);

        // Firefox options
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setBinary("C:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe");
        //firefoxOptions.setBinary(new FirefoxBinary(Optional.empty()));
        firefoxOptions.addArguments(new ArrayList<String>());
        firefoxOptions.setLogLevel(null);
        //firefoxOptions.addPreference();
        firefoxOptions.setProfile(null);
//        capabilities.setCapability("moz", firefoxOptions);
        capabilities.setCapability("moz:firefoxOptions", firefoxOptions);

        WebDriver driver = new FirefoxDriver(capabilities);

        /* Use this driver to visit Google */
        driver.get("http://www.google.com");
        /* Alternatively the same thing can be done like this */
        // driver.navigate().to("http://www.google.com");

        // Find the text input element by its name */
        WebElement element = driver.findElement(By.name("q"));

        /* Enter something to search for */
        element.sendKeys("Cheese!");

        /* Now submit the form. WebDriver will find the form for us from the element */
        element.submit();

        /* Check the title of the page */
        System.out.println("Page title is: " + driver.getTitle());

        /* Google's search is rendered dynamically with JavaScript.
           Wait for the page to load, timeout after 10 seconds */
        (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.getTitle().toLowerCase().startsWith("cheese!");
            }
        });

        /* Should see: "cheese! - Google Search" */
        System.out.println("Page title is: " + driver.getTitle());

        /* Close the browser */
        driver.quit();

        return;
    }
}
