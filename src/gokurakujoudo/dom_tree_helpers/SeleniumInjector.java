package gokurakujoudo.dom_tree_helpers;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nacos on 4/15/2017.
 */
public class SeleniumInjector {
    public static void main(String... args) {
        String googleScholarURL = "https://scholar.google.com/scholar?hl=en&q=database&as_sdt=1%2C14&as_sdtp=&oq=";
        String HTML = null;

        SeleniumInjector seleniumInjector = new SeleniumInjector(DevPlatform.WINDOWS);
        if (seleniumInjector.render(googleScholarURL) == 0) {
            HTML = seleniumInjector.getHTML();

            /* Save the injected HTML into file */
            String outfile = "renderedHTML/googleScholar.html";
            if (seleniumInjector.saveToFile(outfile) == 0)
                System.out.println("Successfully saved to " + outfile);
        }

        seleniumInjector.close();
        return;
    }

    /* Param */
    public enum DevPlatform {
        WINDOWS,
        MACOS,
        LINUX,
        UNKNOWN
    }

    /* Tool */
    private WebDriver _driver;

    /* Output */
    private String _Title;
    public String getTitle(){
        return _Title;
    }
    private String _HTML;
    public String getHTML(){
        return _HTML;
    }

    /**
     * Constructor
     * @param devPlatform specify current platform (Windows only)
     */
    public SeleniumInjector(DevPlatform devPlatform){
        /* Selenium capacities */
        DesiredCapabilities capabilities = DesiredCapabilities.firefox();
        capabilities.setCapability("marionette", true);
        capabilities.setBrowserName("firefox");
        capabilities.setVersion("");

        switch (devPlatform) {
            case WINDOWS:
                System.setProperty("webdriver.gecko.driver", "lib\\geckodriver.exe");
                capabilities.setPlatform(Platform.WIN10);

                // Firefox options
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.setBinary("C:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe");
                firefoxOptions.addArguments(new ArrayList<String>());
                firefoxOptions.setLogLevel(null);
                //firefoxOptions.addPreference();
                firefoxOptions.setProfile(null);

                capabilities.setCapability("moz:firefoxOptions", firefoxOptions);
                break;

            case MACOS:
            case LINUX:
            case UNKNOWN:
                break;
        }

        _driver = new FirefoxDriver(capabilities);
    }

    /**
     * Render the page and perform the injection
     * @param URL
     * @return 0 on success
     */
    public int render(String URL){
        try {
            _driver.get(URL);
            /* Alternatively the same thing can be done like this */
            // driver.navigate().to("http://www.google.com");

            /* Traverse through all the elements and inject CSS info */
            List<WebElement> elements = _driver.findElements(By.cssSelector("*"));

            for (WebElement element : elements) {
                String text = element.getText();

                String marginR = element.getCssValue("margin-right"); // In the format of "31px"
                String marginL = element.getCssValue("margin-left");


                Dimension dimension = element.getSize();
                String height = dimension.getHeight() + "px";
                String width = dimension.getWidth() + "px";

                if (false) {
                    ((JavascriptExecutor) _driver).executeScript(
                            "var ele=arguments[0]; ele.style.marginLeft = arguments[1];", element, marginL);
                    ((JavascriptExecutor) _driver).executeScript(
                            "var ele=arguments[0]; ele.style.marginRight = arguments[1];", element, marginR);
                    ((JavascriptExecutor) _driver).executeScript(
                            "var ele=arguments[0]; ele.style.height = arguments[1];", element, height);
                    ((JavascriptExecutor) _driver).executeScript(
                            "var ele=arguments[0]; ele.style.width = arguments[1];", element, width);
                } else {
                    ((JavascriptExecutor) _driver).executeScript(
                            "var ele=arguments[0]; ele.setAttribute('width', arguments[1])", element, width);
                    ((JavascriptExecutor) _driver).executeScript(
                            "var ele=arguments[0]; ele.setAttribute('height', arguments[1])", element, height);
                    ((JavascriptExecutor) _driver).executeScript(
                            "var ele=arguments[0]; ele.setAttribute('margin-left', arguments[1])", element, marginL);
                    ((JavascriptExecutor) _driver).executeScript(
                            "var ele=arguments[0]; ele.setAttribute('margin-right', arguments[1])", element, marginR);
                }

                //String style = element.getAttribute("style");
                continue;
            }

            _HTML = _driver.getPageSource();
            _Title = _driver.getTitle();
            return 0;

        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Write the injected HTML into a .html file
     * @param filename
     * @return 0 on success
     */
    public int saveToFile(String filename){
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename));
            bufferedWriter.write(_HTML);
            bufferedWriter.flush();
            bufferedWriter.close();

            return 0;
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
            ioe.printStackTrace();
            return -1;
        }
    }

    /**
     * Close the Firefox driver
     */
    public void close(){
        _driver.close();
    }

}
