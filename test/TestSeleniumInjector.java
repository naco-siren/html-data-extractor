import gokurakujoudo.injection.SeleniumInjector;
import org.junit.Test;

/**
 * Created by nacos on 4/23/2017.
 */
public class TestSeleniumInjector {
    private static final String GOOGLE_SCHOLAR_URL = "http://www.bestbuy.com/site/searchpage.jsp?st=earphones&_dyncharset=UTF-8&id=pcat17071&type=page&sc=Global&cp=1&nrp=&sp=&qp=&list=n&af=true&iht=y&usc=All+Categories&ks=960&keys=keys";
    private static final String GOOGLE_SCHOLAR_TITLE = "bestbuy";


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
}
