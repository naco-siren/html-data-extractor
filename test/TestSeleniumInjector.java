import gokurakujoudo.dom_tree_helpers.SeleniumInjector;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import java.io.File;

/**
 * Created by nacos on 4/23/2017.
 */
public class TestSeleniumInjector {
    private static final String GOOGLE_SCHOLAR_URL = "https://scholar.google.com/scholar?hl=en&q=database&as_sdt=1%2C14&as_sdtp=&oq=";
    private static final String GOOGLE_SCHOLAR_TITLE = "googleScholar";


    /**
     * Test CSS injection into HTML document
     * @throws Exception
     */
    @Test
    public void testCSSInjection() throws Exception {
        testCSSInjectionForURL(GOOGLE_SCHOLAR_URL, GOOGLE_SCHOLAR_TITLE);


    }
    public void testCSSInjectionForURL(String URL, String fileName) throws Exception {
        String HTML = null;

        SeleniumInjector seleniumInjector = new SeleniumInjector(SeleniumInjector.DevPlatform.WINDOWS);
        if (seleniumInjector.render(URL) == 0) {
            HTML = seleniumInjector.getHTML();

            /* Save the injected HTML into file */
            String outfile = fileName + ".html";
            if (seleniumInjector.saveToFile(outfile) == 0)
                System.out.println("=== Successfully saved to " + outfile);
        }

        seleniumInjector.close();
        return;

    }


    /**
     * Test smart unwrapping for <a> elements
     * @throws Exception
     */
    @Test
    public void testSmartUnwrapping() throws Exception {
        // Part of Google Scholar
        testSmartUnwrappingOnFile("googleScholarPart1");


        // Google Scholar
        testSmartUnwrappingOnFile(GOOGLE_SCHOLAR_TITLE);


    }
    public void testSmartUnwrappingOnFile(String fileName) throws Exception {
        String inFileName = SeleniumInjector.INJECTED_HTML_OUTPUT_DIR + File.separator + fileName + ".html";

        Document document = Jsoup.parse(new File(inFileName), "UTF-8", "");
        Element body = document.body();

        SeleniumInjector.smartUnwrap(document.outerHtml());



    }
}
