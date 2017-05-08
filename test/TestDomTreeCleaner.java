import naco_siren.github.htmldataextractor.dom_tree_helpers.DomTreeCleaner;
import naco_siren.github.htmldataextractor.injection.SeleniumInjector;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import java.io.File;

/**
 * Created by nacos on 4/24/2017.
 */
public class TestDomTreeCleaner {
    private static final String GOOGLE_SCHOLAR_TITLE = "googleScholar";


    /**
     * Test smart unwrapping for <a> elements
     * @throws Exception
     */
    @Test
    public void testSmartUnwrapping() throws Exception {
        // <div> blank <a> blank <a> blank <a> blank </div>
        //String result1 = testSmartUnwrappingOnFile("googleScholarPart1");

        // <div> text <a> text <a> text </div>
        //String result2 = testSmartUnwrappingOnFile("googleScholarPart2");



        // Google Scholar
        String result3 = testSmartUnwrappingOnFile(GOOGLE_SCHOLAR_TITLE);

        return;
    }

    public String testSmartUnwrappingOnFile(String fileName) throws Exception {
        String inFileName = SeleniumInjector.INJECTED_HTML_OUTPUT_DIR + File.separator + fileName + ".html";

        Document document = Jsoup.parse(new File(inFileName), "UTF-8", "");
        Element body = document.body();

        DomTreeCleaner.trimDOMTree(body);
        DomTreeCleaner.smartUnwrapDOMTree(body);

        String result = body.outerHtml();

        return result;
    }
}
