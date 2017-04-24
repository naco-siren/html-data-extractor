import gokurakujoudo.dom_tree_helpers.SeleniumInjector;
import org.junit.Test;

/**
 * Created by nacos on 4/23/2017.
 */
public class TestSeleniumInjector {
    @Test
    public void testCSSInjection() throws Exception {
        String googleScholarURL = "https://scholar.google.com/scholar?hl=en&q=database&as_sdt=1%2C14&as_sdtp=&oq=";
        String HTML = null;

        SeleniumInjector seleniumInjector = new SeleniumInjector(SeleniumInjector.DevPlatform.WINDOWS);
        if (seleniumInjector.render(googleScholarURL) == 0) {
            HTML = seleniumInjector.getHTML();

            /* Save the injected HTML into file */
            String outfile = "googleScholar.html";
            if (seleniumInjector.saveToFile(outfile) == 0)
                System.out.println("Successfully saved to " + outfile);
        }

        seleniumInjector.close();
        return;
    }


    @Test
    public void testSmartUnwrapping() throws Exception {


    }
}
