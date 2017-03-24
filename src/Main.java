import apted.costmodel.StringUnitCostModel;
import apted.distance.APTED;
import apted.node.Node;
import apted.node.StringNodeData;
import apted.parser.BracketStringInputParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Main {

    public static void main(String[] args) {

        /* 1. Jsoup parsing HTML part */

        Document doc = null;
        try {
            // a. Use Jsoup directly connect to and parse a given URL
            String googleScholar = "https://scholar.google.com/scholar?hl=zh-CN&q=information+extraction&btnG=&lr=";
            doc = Jsoup.connect(googleScholar).get();

            // b. Direct parse a HTML String
            //doc = Jsoup.parse("<body>This is a fake body</body>");
        } catch (Exception e){
            e.printStackTrace();
        }
        String title = doc.title();
        Element resultsRoot = doc.getElementById("gs_ccl_results");
        Elements results = resultsRoot.children();


        /* 2. Tree Edit Distance part */

        // Parse the input.
        BracketStringInputParser parser = new BracketStringInputParser();
        Node<StringNodeData> t1 = parser.fromString("{div{span}{div{a}{a}}}");
        Node<StringNodeData> t2 = parser.fromString("{div{span}{div{div}}{a}}");

        // Initialise APTED.
        APTED<StringUnitCostModel, StringNodeData> apted = new APTED<>(new StringUnitCostModel());

        // This cast is safe due to unit cost.
        int result = (int)apted.computeEditDistance(t1, t2);

        return;
    }
}
