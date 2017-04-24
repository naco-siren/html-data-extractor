package gokurakujoudo.dom_tree_helpers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.NodeTraversor;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nacos on 4/15/2017.
 */
public class SeleniumInjector {
    /* Param */
    public static final String INJECTED_HTML_OUTPUT_DIR = "injectedHTML";
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
                String paddingL = element.getCssValue("padding-left");
                String paddingR = element.getCssValue("padding-right");


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
                    /* The dimensions */
                    ((JavascriptExecutor) _driver).executeScript(
                            "var ele=arguments[0]; ele.setAttribute('width', arguments[1])", element, width);
                    ((JavascriptExecutor) _driver).executeScript(
                            "var ele=arguments[0]; ele.setAttribute('height', arguments[1])", element, height);

                    /* The margins */
                    ((JavascriptExecutor) _driver).executeScript(
                            "var ele=arguments[0]; ele.setAttribute('margin-left', arguments[1])", element, marginL);
                    ((JavascriptExecutor) _driver).executeScript(
                            "var ele=arguments[0]; ele.setAttribute('margin-right', arguments[1])", element, marginR);

                    /* The paddings */
                    ((JavascriptExecutor) _driver).executeScript(
                            "var ele=arguments[0]; ele.setAttribute('padding-left', arguments[1])", element, paddingL);
                    ((JavascriptExecutor) _driver).executeScript(
                            "var ele=arguments[0]; ele.setAttribute('padding-right', arguments[1])", element, paddingR);

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


    public static void smartUnwrap(String html){
        Document document = Jsoup.parse(html);
        Element body = document.body();

        AElementVisitor aElementVisitor = new AElementVisitor();
        NodeTraversor unwrappingTraversor = new NodeTraversor(aElementVisitor);
        unwrappingTraversor.traverse(body);


        String newHTML = document.outerHtml();
        return;
    }


    /**
     * Write the injected HTML into a .html file
     * @param filename
     * @return 0 on success
     */
    public int saveToFile(String filename){
        try {
            /* Create output directory if not exists */
            File file =new File(INJECTED_HTML_OUTPUT_DIR);
            if  (file.exists() == false && file.isDirectory() == false) {
                file.mkdir();
            }

            /* Write HTML into the file */
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(INJECTED_HTML_OUTPUT_DIR + File.separator + filename));
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
