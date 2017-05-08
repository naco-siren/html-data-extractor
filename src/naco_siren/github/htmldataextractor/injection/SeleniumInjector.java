package naco_siren.github.htmldataextractor.injection;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static naco_siren.github.htmldataextractor.injection.InjectionUtils.*;

/**
 * Created by nacos on 4/15/2017.
 */
public class SeleniumInjector {
    /* Param */
    public static final String INJECTED_HTML_OUTPUT_DIR = "injected_HTML";
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
    public SeleniumInjector(DevPlatform devPlatform, boolean invisible){
        switch (devPlatform) {
            case WINDOWS:
                if (invisible == false) {
                    /* Selenium capacities */
                    DesiredCapabilities capabilities = DesiredCapabilities.firefox();
                    capabilities.setCapability("marionette", true);
                    capabilities.setBrowserName("firefox");
                    capabilities.setVersion("");
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
                    _driver = new FirefoxDriver(capabilities);

                } else {
                    DesiredCapabilities caps = new DesiredCapabilities();
                    caps.setJavascriptEnabled(true);
                    caps.setCapability("takesScreenshot", true);
                    caps.setCapability(
                            PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                            "lib\\phantomjs-2.1.1-windows\\bin\\phantomjs.exe"
                    );
                    _driver = new PhantomJSDriver(caps);
                }
                break;

            case MACOS:
            case LINUX:
            case UNKNOWN:
                break;
        }


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
            ((JavascriptExecutor) _driver).executeScript(
                "var elements = document.body.getElementsByTagName('*');" +
                "for (var i = 0; i < elements.length; i++)" +
                "{" +
                    "var ele = elements[i];" +

                    /* Dimensions */
                    //"var positionInfo = ele.getBoundingClientRect();" +
                    //"var height = Math.round(positionInfo.height);" +
                    //"var width = Math.round(positionInfo.width);" +
                    "var height = Math.round(ele.offsetHeight);" +
                    "var width = Math.round(ele.offsetWidth);" +
                    "var area = width * height;" +
                    "ele.setAttribute('" + WIDTH + "', width);" +
                    "ele.setAttribute('" + HEIGHT + "', height);" +
                    "ele.setAttribute('" + AREA + "', area);" +

                    /* Paddings */
                    "var paddingL = window.getComputedStyle(ele).paddingLeft;" +
                    "var paddingR = window.getComputedStyle(ele).paddingRight;" +
                    "ele.setAttribute('" + PADDING_LEFT + "', paddingL);" +
                    "ele.setAttribute('" + PADDING_RIGHT + "', paddingR);" +

                    /* Margins */
                    "var marginL = window.getComputedStyle(ele).marginLeft;" +
                    "var marginR = window.getComputedStyle(ele).marginRight;" +
                    "ele.setAttribute('" + MARGIN_LEFT + "', marginL);" +
                    "ele.setAttribute('" + MARGIN_RIGHT + "', marginR);" +
                "}"
            );

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

            /* Legalize the file name */
            filename = filename.replaceAll("[^a-zA-Z0-9.-]", "_");

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
