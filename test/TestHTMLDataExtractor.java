import gokurakujoudo.HTMLDataExtractor;
import gokurakujoudo.data.DataGroup;
import gokurakujoudo.data.DataGroups;
import org.json.JSONObject;
import org.json.XML;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by nacos on 4/26/2017.
 */
public class TestHTMLDataExtractor {
    @Test
    public void testWebpages() throws Exception {
        String[] URLs = new String[]{
                "https://scholar.google.com/scholar?hl=en&q=database&as_sdt=1%2C14&as_sdtp=&oq="

        };
        String[] TITLEs = new String[] {
                "Google Scholar"
        };


        for (int i = 0; i < URLs.length; i++) {
            System.out.println("=== Testing on " + TITLEs[i] + " ===");
            assert (testWebpage(URLs[i], true, true) == 8);
            System.out.println();
        }

        return;
    }

    public int testWebpage(String URL, boolean outputHTML, boolean outputJSON) {
        /* Instantiate an HTMLDataExtractor */
        HTMLDataExtractor htmlDataExtractor = new HTMLDataExtractor();

        /* Read from URL */
        htmlDataExtractor.readFromURL(URL);

        /* Clean DOM tree */
        htmlDataExtractor.cleanDomTree();

        /* Perform extraction */
        htmlDataExtractor.setMinResultSize(2);
        if (htmlDataExtractor.extract() == 0) {

            /* Refine results using default strategy */
            htmlDataExtractor.refine();

            /* Collect and clean up the results */
            DataGroups results = htmlDataExtractor.getResults();
            results.clean();

            /* Output the results */
            int dataCount = 0;
            for (int i = 0; i < results.size(); i++) {
                DataGroup dataGroup = results.get(i);
                dataCount += dataGroup.size();

                System.out.println();
                System.out.println("*** No. " + i + ", " + dataGroup);

                /* HTML format */
                if (outputHTML) {
                    ArrayList<String> dataHTMLs = dataGroup.getHTMLs();
                    for (String dataHTML : dataHTMLs) {
                        System.out.println(dataHTML);
                        System.out.println();
                    }
                }

                /* JSON format */
                if (outputJSON) {
                    ArrayList<String> dataJSONs = dataGroup.getJSONs();
                    for (String dataJSON : dataJSONs)
                        System.out.println(dataJSON);
                }
            }

            return dataCount;
        } else {
            return -1;
        }
    }
}