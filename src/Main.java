import apted.costmodel.StringUnitCostModel;
import apted.distance.APTED;
import apted.node.Node;
import apted.node.StringNodeData;
import apted.parser.BracketStringInputParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeTraversor;


public class Main {

    public static void main(String[] args) throws Exception {

        /* 1. Jsoup parsing HTML part */
        part1();

        /* 2. Transforming Jsoup's tree into APTED project's input format */

        /* Html1:
                  body
                /     \
               A.      B.
             /  \     / \
           A.1 A.2  B.1 B.2
         */
        String html1 =
                "<html><body><div>A.<div>A.1</div><div>A.2</div></div><div>B.<div>B.1</div><div>B.2</div></div></body></html>\n";
        String tree1 = part2(html1);

        /* Html2:
                  body
                /     \
               A.     B.
              /     /  \  \
            A.1   B.1 B.2 B.3
         */
        String html2 =
                "<html><body><div>A.<div>A.1</div></div><div>B.<div>B.1</div><div>B.2</div><div>B.3</div></div></body></html>\n";
        String tree2 = part2(html2);


        /* 3. Tree Edit Distance part */
        int diff = part3(tree1, tree2);
        System.out.println("Difference: " + diff);

    }

    public static void part1(){
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
    }

    public static String part2(String html) throws Exception{
        Document document = Jsoup.parse(html);


        APTEDNodeVisitor aptedNodeVisitor = new APTEDNodeVisitor();
        NodeTraversor traversor = new NodeTraversor(aptedNodeVisitor);
        traversor.traverse(document);

        return document.body().APTEDTreeStructure;
    }


    public static int part3(String tree1, String tree2){

        // Parse the input.
        BracketStringInputParser parser = new BracketStringInputParser();
        Node<StringNodeData> t1 = parser.fromString(tree1);     //"{div{span}{div{a}{a}}}");
        Node<StringNodeData> t2 = parser.fromString(tree2);     //"{div{span}{div{div}}{a}}");

        // Initialise APTED.
        APTED<StringUnitCostModel, StringNodeData> apted = new APTED<>(new StringUnitCostModel());

        // This cast is safe due to unit cost.
        return  (int)apted.computeEditDistance(t1, t2);
    }
}
