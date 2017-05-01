import gokurakujoudo.HTMLDataExtractor;
import gokurakujoudo.data.DataGroup;
import gokurakujoudo.data.DataGroups;
import org.json.JSONObject;
import org.json.XML;
import org.junit.Test;
import gokurakujoudo.data.Prettify_json;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by nacos on 4/26/2017.
 */
public class TestHTMLDataExtractor {
    @Test
    public void testWebpages() throws Exception {

        /* TODO: Add more test cases and analyze! */
        String[] titles = new String[] {
                "Google Scholar",
                //"Bestbuy - earphones",
                //"Amazon - book light"
        };
        String[] URLs = new String[]{
                "https://scholar.google.com/scholar?hl=en&q=database&btnG=&as_sdt=1%2C14&as_sdtp="
                //"https://stackoverflow.com/",
                //"http://www.bestbuy.com/site/searchpage.jsp?st=earphones&_dyncharset=UTF-8&id=pcat17071&type=page&sc=Global&cp=1&nrp=&sp=&qp=&list=n&af=true&iht=y&usc=All+Categories&ks=960&keys=keys",
                //"https://www.amazon.com/s/ref=nb_sb_ss_c_1_10?url=search-alias%3Dstripbooks&field-keywords=book+light&sprefix=book+light%2Cstripbooks%2C151&crid=3LC02T51FKVHJ"
        };
        int[] expectedOutputDataCounts = new int[] {
                8
                //24,
                //12
        };


        /* DO NOT MODIFY THIS! */
        for (int i = 0; i < URLs.length; i++) {
            System.out.println("=== Testing on " + titles[i] + " ===");
            //assert (testWebpage(URLs[i], true, true) == expectedOutputDataCounts[i]);
            try {
                testWebpage(URLs[i], true, true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println();
        }

        return;
    }

    public int testWebpage(String URL, boolean outputHTML, boolean outputJSON) throws IOException {
        /* Instantiate an HTMLDataExtractor */
        HTMLDataExtractor htmlDataExtractor = new HTMLDataExtractor();

        /* Read from URL */
        htmlDataExtractor.readFromURL(URL);

        /* Clean DOM tree */
        htmlDataExtractor.cleanDomTree();

        /* Perform extraction */
        htmlDataExtractor.setMinResultSize(2);
        if (htmlDataExtractor.extract(true, true) == 0) {

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
                    for (String dataJSON : dataJSONs) {
                        //TODO: parse json to table
                        //Prettify_json.readd(dataJSON);
                        System.out.println(dataJSON);
                        System.out.println();
                    }

                }
            }

            return dataCount;
        } else {
            return -1;
        }
    }



    /**
     * Test on Bestbuy, keyword: earphones
     * @throws Exception
     */
    @Test
    public void testOnBestBuy() throws Exception {
        String url = "http://www.bestbuy.com/site/searchpage.jsp?st=earphones&_dyncharset=UTF-8&id=pcat17071&type=page&sc=Global&cp=1&nrp=&sp=&qp=&list=n&af=true&iht=y&usc=All+Categories&ks=960&keys=keys";
        testWebpage(url, true, true);
        return;
    }

    /**
     * Test on Amazon, keyword: book light
     * @throws Exception
     */
    @Test
    public void testOnAmazon() throws Exception {
        String url = "https://www.amazon.com/s/ref=nb_sb_ss_c_1_10?url=search-alias%3Dstripbooks&field-keywords=book+light&sprefix=book+light%2Cstripbooks%2C151&crid=3LC02T51FKVHJ";
        testWebpage(url, true, true);
        return;

    }

}
