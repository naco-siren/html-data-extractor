import gokurakujoudo.dom_tree_helpers.DomTreeCleaner;
import gokurakujoudo.dom_tree_helpers.SeleniumInjector;
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
        // <div> T <a> T <a> T <a> T </div>
        //testSmartUnwrappingOnFile("googleScholarPart1");

        // <div><a><a><a></div>
        testSmartUnwrappingOnFile("googleScholarPart2");


        // Google Scholar
        //testSmartUnwrappingOnFile(GOOGLE_SCHOLAR_TITLE);


    }
    public void testSmartUnwrappingOnFile(String fileName) throws Exception {
        String inFileName = SeleniumInjector.INJECTED_HTML_OUTPUT_DIR + File.separator + fileName + ".html";

        Document document = Jsoup.parse(new File(inFileName), "UTF-8", "");
        Element body = document.body();

        DomTreeCleaner.smartUnwrap(body.outerHtml());

        System.out.println(body.html());

        return;
    }
}
