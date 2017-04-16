package gokurakujoudo.dom_tree_helpers;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nacos on 4/15/2017.
 */
public class SeleniumRenderer {
    public static void main(String... args) {
        SeleniumRenderer seleniumRenderer = new SeleniumRenderer();
        seleniumRenderer.get("https://scholar.google.com/scholar?hl=en&q=database&as_sdt=1%2C14&as_sdtp=&oq=");

        return;
    }


    private WebDriver _driver;

    public SeleniumRenderer(){
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

        _driver = new FirefoxDriver(capabilities);
    }


    public void get(String URL){

        _driver.get(URL);
        /* Alternatively the same thing can be done like this */
        // driver.navigate().to("http://www.google.com");

        // Find the text input element by its name */
        //WebElement element = _driver.findElement(By.tagName("a"));
        List<WebElement> elements = _driver.findElements(By.tagName("a"));

        for (WebElement element : elements) {
            String text = element.getText();

            String marginR = element.getCssValue("margin-right"); // In the format of "31px"
            String marginL = element.getCssValue("margin-left");


            Dimension dimension = element.getSize();
            int height = dimension.getHeight();
            int width = dimension.getWidth();


        }

        return;
    }




}
