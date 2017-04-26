package gokurakujoudo.injection;

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

import static gokurakujoudo.injection.InjectionUtils.*;

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

    /* Settings */
    private boolean _injectDimensionInfo = true;
    private boolean _injectMarginInfo = true;
    private boolean _injectPaddingInfo = true;
    private boolean _injectFontInfo = false;

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

            /* Traverse through all the elements and inject CSS info */
            List<WebElement> elements = _driver.findElements(By.cssSelector("*"));

            for (WebElement element : elements) {
                String text = element.getText();

                /* Dimensions */
                if (_injectDimensionInfo) {
                    Dimension dimension = element.getSize();

                    int height = dimension.getHeight();
                    int width = dimension.getWidth();
                    int area = height * width;

                    ((JavascriptExecutor) _driver).executeScript(
                            "var ele=arguments[0]; ele.setAttribute('" + WIDTH + "', arguments[1])", element, width + "px");
                    ((JavascriptExecutor) _driver).executeScript(
                            "var ele=arguments[0]; ele.setAttribute('" + HEIGHT + "', arguments[1])", element, height + "px");
                    ((JavascriptExecutor) _driver).executeScript(
                            "var ele=arguments[0]; ele.setAttribute('" + AREA + "', arguments[1])", element, "" + area);
                }

                /* Margins */
                if (_injectMarginInfo) {
                    String marginR = element.getCssValue(MARGIN_RIGHT);
                    String marginL = element.getCssValue(MARGIN_LEFT);

                    ((JavascriptExecutor) _driver).executeScript(
                            "var ele=arguments[0]; ele.setAttribute('" + MARGIN_LEFT + "', arguments[1])", element, marginL);
                    ((JavascriptExecutor) _driver).executeScript(
                            "var ele=arguments[0]; ele.setAttribute('" + MARGIN_RIGHT + "', arguments[1])", element, marginR);
                }

                /* Padding */
                if (_injectPaddingInfo) {
                    String paddingL = element.getCssValue(PADDING_LEFT);
                    String paddingR = element.getCssValue(PADDING_RIGHT);

                    ((JavascriptExecutor) _driver).executeScript(
                            "var ele=arguments[0]; ele.setAttribute('" + PADDING_LEFT + "', arguments[1])", element, paddingL);
                    ((JavascriptExecutor) _driver).executeScript(
                            "var ele=arguments[0]; ele.setAttribute('" + PADDING_RIGHT + "', arguments[1])", element, paddingR);
                }

                /* Fonts */
                if (_injectFontInfo) {
                    String fontFamily = element.getCssValue(FONT_FAMILY);
                    String fontStyle = element.getCssValue(FONT_STYLE);
                    String fontSize = element.getCssValue(FONT_SIZE);
                    String fontWeight = element.getCssValue(FONT_WEIGHT);
                    //String fontVariant = element.getCssValue(FONT_VARIANT);

                    ((JavascriptExecutor) _driver).executeScript(
                            "var ele=arguments[0]; ele.setAttribute('" + FONT_FAMILY + "', arguments[1])", element, fontFamily);
                    ((JavascriptExecutor) _driver).executeScript(
                            "var ele=arguments[0]; ele.setAttribute('" + FONT_SIZE + "', arguments[1])", element, fontSize);
                    ((JavascriptExecutor) _driver).executeScript(
                            "var ele=arguments[0]; ele.setAttribute('" + FONT_STYLE + "', arguments[1])", element, fontStyle);
                    ((JavascriptExecutor) _driver).executeScript(
                            "var ele=arguments[0]; ele.setAttribute('" + FONT_WEIGHT + "', arguments[1])", element, fontWeight);
                }



                //String style = element.getAttribute("style");
//                ((JavascriptExecutor) _driver).executeScript(
//                        "var ele=arguments[0]; ele.style.marginLeft = arguments[1];", element, marginL);
//                ((JavascriptExecutor) _driver).executeScript(
//                        "var ele=arguments[0]; ele.style.marginRight = arguments[1];", element, marginR);
//                ((JavascriptExecutor) _driver).executeScript(
//                        "var ele=arguments[0]; ele.style.height = arguments[1];", element, height);
//                ((JavascriptExecutor) _driver).executeScript(
//                        "var ele=arguments[0]; ele.style.width = arguments[1];", element, width);

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
        _driver.quit();
    }

}
