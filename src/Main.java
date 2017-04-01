import gokurakujoudo.data.DataGroup;
import gokurakujoudo.HTMLDataExtractor;
import gokurakujoudo.data.DataGroups;
import org.json.JSONObject;
import org.json.XML;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws Exception {
        /* Instantiate an HTMLDataExtractor */
        HTMLDataExtractor htmlDataExtractor = new HTMLDataExtractor();

        /* Read from URL */
        String googleScholarURL = "https://scholar.google.com/scholar?hl=en&q=database&as_sdt=1%2C14&as_sdtp=&oq=";
        htmlDataExtractor.readFromURL(googleScholarURL);

        /* Clean DOM tree */

        htmlDataExtractor.cleanDomTree();

        /* Perform extraction */
        htmlDataExtractor.setMinResultSize(2);
        if (htmlDataExtractor.extract() == 0) {
            /* Refine results using default strategy */
            htmlDataExtractor.refine();


            DataGroups results = htmlDataExtractor.getResults();
            results.clean();

            /* Output the results */
            for (int i = 0; i < results.size(); i++) {
                DataGroup dataGroup = results.get(i);

                System.out.println("=== No. " + i + ", " + dataGroup);
                //System.out.println(dataGroup.getDataHTMLs());
                ArrayList<String> dataHTMLs = dataGroup.getDataHTMLs();

                for (String dataHTML : dataHTMLs) {
                    JSONObject jsonObject = XML.toJSONObject(dataHTML);
//                    jsonObject.remove("id");
//                    jsonObject.remove("class");
//                    jsonObject.remove("href");
//                    jsonObject.remove("href");


                    String jsonString = jsonObject.toString();
                    System.out.println(jsonString);
                    System.out.println();
                }

                System.out.println();
            }
        }

        return;
    }

}
