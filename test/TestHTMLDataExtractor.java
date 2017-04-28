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

        /* TODO: Add more test cases and analyze! */
        String[] titles = new String[] {
                "Google Scholar"
        };
        String[] URLs = new String[]{
                "https://scholar.google.com/scholar?hl=en&q=database&as_sdt=1%2C14&as_sdtp=&oq="

        };
        int[] expectedOutputDataCounts = new int[] {
                8
        };


        /* DO NOT MODIFY THIS! */
        for (int i = 0; i < URLs.length; i++) {
            System.out.println("=== Testing on " + titles[i] + " ===");
            assert (testWebpage(URLs[i], true, true) == expectedOutputDataCounts[i]);
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

    @Test
    public void testOnBestBuy() throws Exception {
        String url = "http://www.bestbuy.com/site/searchpage.jsp?cp=1&searchType=search&st=surface&_dyncharset=UTF-8&id=pcat17071&type=page&sc=Global&nrp=&sp=&qp=brand_facet%3DBrand~Microsoft&list=n&af=true&iht=y&usc=All%20Categories&ks=960&keys=keys";
        testWebpage(url, true, true);
        return;
    }


    /**
     * TODO: Sun Hao will use this test case to make use of area information
     * @throws Exception
     */
    @Test
    public void testOnAmazon() throws Exception {
        String url = "https://www.amazon.com/s/ref=nb_sb_noss?url=search-alias%3Daps&field-keywords=playstation4&rh=i%3Aaps%2Ck%3Aplaystation4";
        testWebpage(url, true, true);
        return;

    }

}
