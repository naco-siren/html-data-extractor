import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello World!");

        Document doc = null;
        try {
            doc = Jsoup.connect("https://www.google.com").get();
        } catch (Exception e){
            e.printStackTrace();
        }
        String title = doc.title();

        return;
    }
}
