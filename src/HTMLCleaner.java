import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by haosun on 3/25/17.
 */
public class HTMLCleaner {
    String baseURL;
    String[] removeTagNames;

    public HTMLCleaner(String baseURL, String[] removeTagNames){
        this.baseURL = baseURL;
        this.removeTagNames = removeTagNames;
    }

    public HTMLCleaner(){
        baseURL = null;
        removeTagNames = null;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    public String getBaseURL() {
        return baseURL;
    }

    public void setRemoveTagNames(String[] removeTagNames) {
        this.removeTagNames = removeTagNames;
    }

    public String[] getRemoveTagNames() {
        return removeTagNames;
    }

    public Document clean() throws IOException {
        Document doc = Jsoup.connect(baseURL).get();
        Elements elesToRemove = new Elements();

        for(String removeTagName : removeTagNames){
            elesToRemove.addAll(doc.getElementsByTag(removeTagName));
        }

        for(Element ele : elesToRemove){
            ele.remove();
        }

        return doc;

    }

    public static void main(String[] args) throws IOException {
        String[] removeTagNames = new String[]{"meta", "script", "title", "style"};
        String URL = "https://scholar.google.com/scholar?hl=en&q=database&as_sdt=1%2C14&as_sdtp=&oq=";
        HTMLCleaner htmlCleaner = new HTMLCleaner(URL, removeTagNames);

        Document doc = htmlCleaner.clean();

        return;
    }
}
