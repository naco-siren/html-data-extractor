import org.jsoup.nodes.Node;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws Exception {
        /* Instantiate an HTMLDataExtractor */
        HTMLDataExtractor htmlDataExtractor = new HTMLDataExtractor();

        /* Read from URL */
        String googleScholarURL = "https://scholar.google.com/scholar?hl=en&q=database&as_sdt=1%2C14&as_sdtp=&oq=";
        htmlDataExtractor.readFromURL(googleScholarURL);

        /* Clean DOM tree */
        String[] removeTagNames = new String[]{"meta", "script", "title", "style"};
        htmlDataExtractor.cleanDomTree(removeTagNames);

        /* Perform extraction */
        htmlDataExtractor.setMinResultSize(4);
        if (htmlDataExtractor.extract() == 0) {
            ArrayList<ArrayList<Node>> results = htmlDataExtractor.getResults();
        }

        return;
    }

}
