import apted.costmodel.StringUnitCostModel;
import apted.distance.APTED;
import apted.node.StringNodeData;
import apted.parser.BracketStringInputParser;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.NodeTraversor;

import java.io.IOException;
import java.util.ArrayList;

/**HTML extractor is used to extract data which users are interested
 * in form a HTML web page.
 * The input is a URL link or a HTML web page document. The data w-
 * ill be extracted and exported into a database-like schema.
 * Created by haosun on 3/27/17.
 */
public class HTMLExtractor {
    private Node root;
    private float proximity = 0.7f;
    private ArrayList<ArrayList<Node>> records = new ArrayList<ArrayList<Node>>();
    private BracketStringInputParser APTEDParser = new BracketStringInputParser();
    private APTED<StringUnitCostModel, StringNodeData> apted = new APTED<>(new StringUnitCostModel());

    public HTMLExtractor(Node root) {
        this.root = root;
    }

    public void setProximity(float proximity) {
        this.proximity = proximity;
    }

    public double getProximity() {
        return proximity;
    }

    private int recordsAdd(Node node) {
        ArrayList<Node> outputList = new ArrayList<Node>();
        int numChilds = node.childNodeSize();
        float voteThreshhold = 0.6f;
        int[] voteResult = new int[numChilds];

        System.out.println(node.numOffSpring + ":");

        for(int i = 0; i < numChilds; i++) {
            for(int j = i + 1; j < numChilds; j++) {
                if(node.childNode(i).APTEDTreeStructure == null || node.childNode(j).APTEDTreeStructure == null) {
                    continue;
                }
                apted.node.Node<StringNodeData> t1 = APTEDParser.fromString(node.childNode(i).APTEDTreeStructure);
                apted.node.Node<StringNodeData> t2 = APTEDParser.fromString(node.childNode(j).APTEDTreeStructure);
                float tmp = (node.childNode(i).numOffSpring + node.childNode(j).numOffSpring) / 2
                        * (1 - proximity);
                //System.out.println("i"+i+"j"+j+"tmp"+tmp+"distance"+apted.computeEditDistance(t1, t2));
                if(tmp >= apted.computeEditDistance(t1, t2)) {
                    voteResult[i]++;
                    voteResult[j]++;
                }
            }
        }

        for(int i = 0; i < numChilds; i++) {
            System.out.print(voteResult[i]);
            if(voteResult[i] >= voteThreshhold * numChilds) {
                outputList.add(node.childNode(i));
            }
        }

        System.out.println("next");

        if(!outputList.isEmpty()) {
            records.add(outputList);
            return outputList.size();
        }else {
            return 0;
        }

    }

    public void recordsExtract(Node root) {

        if(root.childNodeSize() > 0) {
            if(root.childNodeSize() >= 2) {
                int i = recordsAdd(root);
                if(i > 0) {
                    return;
                }else {
                    for(Node node : root.childNodes()) {
                        recordsExtract(node);
                    }
                }
            }else {
                //only one child
                for(Node node : root.childNodes()) {
                    recordsExtract(node);
                }
            }
        }
        return;
    }

    public static void main(String[] args) throws IOException {
        String[] removeTagNames = new String[]{"meta", "script", "title", "style"};
        String URL = "https://scholar.google.com/scholar?hl=en&q=database&as_sdt=1%2C14&as_sdtp=&oq=";
        HTMLCleaner htmlCleaner = new HTMLCleaner(URL, removeTagNames);

        Element mainBody = htmlCleaner.clean();
        //Element searchResult = mainBody.getElementById("gs_ccl");

        APTEDNodeVisitor aptedNodeVisitor = new APTEDNodeVisitor();
        NodeTraversor nodeTraversor = new NodeTraversor(aptedNodeVisitor);
        nodeTraversor.traverse(mainBody);

        HTMLExtractor htmlExtractor = new HTMLExtractor(mainBody);
        htmlExtractor.recordsExtract(htmlExtractor.root);

        ArrayList<ArrayList<Node>> records = htmlExtractor.records;

        return;
    }
}
