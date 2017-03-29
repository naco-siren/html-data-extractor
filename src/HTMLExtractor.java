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

    /**
     * The node's children will vote for each other based on proximity. If two
     * children are close enough, they will give each other one vote. If one c-
     * hild get enough votes, its vote result is true. That means it is one of
     * the possible records which the user is interested in.
     * @param node
     * @return the vote result for each child.
     */
    private boolean[] vote(Node node) {
        int numChilds = node.childNodeSize();
        float voteThreshhold = 0.6f;
        int[] voteCount = new int[numChilds];
        boolean[] voteResult = new boolean[numChilds];

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
                    voteCount[i]++;
                    voteCount[j]++;
                }
            }
        }

        for(int i = 0; i < numChilds; i++) {
            System.out.print(voteCount[i]);
            if(voteCount[i] >= voteThreshhold * numChilds) {
                voteResult[i] = true;
            }else {
                voteResult[i] = false;
            }
        }

        return voteResult;
    }

    /**
     * Decide whether a node's children are the records to output. If they are,
     * they will be put in an arraylist. And the arraylist will be one element
     * of the class' member records.
     * @param node whose children are examined.
     * @return the number of records to output. If it is 0, this node's children
     * are not the records users are interested in.
     */
    private int recordsAdd(Node node) {
        ArrayList<Node> outputList = new ArrayList<Node>();
        int numChilds = node.childNodeSize();
        boolean[] voteResult;

        System.out.println(node.numOffSpring + ":");

        voteResult = vote(node);
        for(int i = 0; i < numChilds; i++) {
            if(voteResult[i]) {
                outputList.add(node.childNode(i));
            }
        }

        if (outputList.isEmpty()) {
            return 0;
        } else {
            records.add(outputList);
            return outputList.size();
        }

    }

    /**
     * Traverse the tree (pre-order) to extract records from the HTML doc.
     * Call method on root, if its children are hit records, move to its
     * Sibling. If not, call method on all its child recursively one by one.
     * @param root the root node of the DOM representation of the HTML doc.
     */
    public void recordsExtract(Node root) {
        if(root.childNodeSize() > 0) {
            if(root.childNodeSize() >= 2) {
                if(recordsAdd(root) > 0) {
                    return;
                }else {
                    for(Node node : root.childNodes()) {
                        recordsExtract(node);
                    }
                }
            }else {
                //only one child
                recordsExtract(root.childNode(0));
            }
        }
        return;
    }

    public void filterByNumber(int minRecordsNum) {
        for(int i = 0; i < records.size(); i++) {
            if(records.get(i).size() < minRecordsNum) {
                records.remove(i);
                i--;
            }
        }
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
        htmlExtractor.filterByNumber(4);

        ArrayList<ArrayList<Node>> records = htmlExtractor.records;

        return;
    }
}
