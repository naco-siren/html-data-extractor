import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello World!");

        Document doc = null;
        try {
            String googleScholar = "https://scholar.google.com/scholar?hl=zh-CN&q=information+extraction&btnG=&lr=";
            doc = Jsoup.connect(googleScholar).get();
        } catch (Exception e){
            e.printStackTrace();
        }
        String title = doc.title();

        Element resultsRoot = doc.getElementById("gs_ccl_results");
        Elements results = resultsRoot.children();

        return;
    }
}
