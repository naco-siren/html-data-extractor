import apted.costmodel.StringUnitCostModel;
import apted.distance.APTED;
import apted.node.StringNodeData;
import apted.parser.BracketStringInputParser;
import org.jsoup.nodes.Node;

import java.util.ArrayList;

/**HTML extractor is used to extract data which users are interested
 * in form a HTML web page.
 * The input is a URL link or a HTML web page document. The data w-
 * ill be extracted and exported into a database-like schema.
 * Created by haosun on 3/27/17.
 */
public class HTMLExtractor {
    private Node root;
    private double proximity = 0.8d;
    private ArrayList<ArrayList<Node>> records;
    private BracketStringInputParser APTEDParser = new BracketStringInputParser();
    private APTED<StringUnitCostModel, StringNodeData> apted = new APTED<>(new StringUnitCostModel());

    public HTMLExtractor(Node root) {
        this.root = root;
    }

    public void setProximity(double proximity) {
        this.proximity = proximity;
    }

    public double getProximity() {
        return proximity;
    }

    private boolean outputExist(Node node) {

        ArrayList<Node> outputList = new ArrayList<Node>();
        int numChilds = node.childNodeSize();
        double voteThreshhold = 0.8d;
        int[] voteResult = new int[numChilds];

        for(int i = 0; i < numChilds; i++) {
            for(int j = i + 1; j < numChilds; j++) {
                apted.node.Node<StringNodeData> t1 = APTEDParser.fromString(node.childNode(i).APTEDTreeStructure);
                apted.node.Node<StringNodeData> t2 = APTEDParser.fromString(node.childNode(j).APTEDTreeStructure);
                double tmp = apted.computeEditDistance(t1, t2) / (node.childNode(i).childNodeSize() +
                    node.childNode(j).childNodeSize());
                if(tmp >= proximity) {
                    voteResult[i]++;
                    voteResult[j]++;
                }
            }
        }

        for(int i = 0; i < numChilds; i++) {
            if((voteResult[i] / numChilds) > voteThreshhold) {
                outputList.add(node.childNode(i));
            }
        }

        if(!outputList.isEmpty()) {
            records.add(outputList);
            return true;
        }else {
            return false;
        }

    }

    public void recordsExtract(Node root) {

        while(root.childNodeSize() > 0){
            if (outputExist(root)) {
                return;
            }else {
                for(Node node : root.childNodes()) {
                    recordsExtract(node);
                }
            }
        }
        return;
    }
}
