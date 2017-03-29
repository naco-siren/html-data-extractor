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
        htmlExtractor.extractData(htmlExtractor._root);
        htmlExtractor.filterByNumber(4);

        ArrayList<ArrayList<Node>> records = htmlExtractor._results;

        return;
    }


    /* Input: */
    private Node _root;

    /* Parameters:  */
    private float _proximity = 0.7f;

    /* Output: */
    private ArrayList<ArrayList<Node>> _results = new ArrayList<ArrayList<Node>>();

    /* Tools:  */
    private BracketStringInputParser _APTEDParser = new BracketStringInputParser();
    private APTED<StringUnitCostModel, StringNodeData> _apted = new APTED<>(new StringUnitCostModel());

    /**
     * Constructor
     * @param root: the root of the input DOM tree
     */
    public HTMLExtractor(Node root) {
        this._root = root;
    }

    /**
     * Customize the proximity
     * @param proximity
     */
    public void setProximity(float proximity) {
        this._proximity = proximity;
    }
    public double getProximity() {
        return _proximity;
    }


    /**
     * Traverse the tree (pre-order) to extract _results from the HTML doc.
     * Call method on root, if its children are hit _results, move to its
     * Sibling. If not, call method on all its child recursively one by one.
     * @param root the root node of the DOM representation of the HTML doc.
     */
    public void extractData(Node root) {
        int childNodeSize = root.childNodeSize();

        if (childNodeSize <= 0) {
            return;

        } else if (childNodeSize == 1){
            extractData(root.childNode(0));

        } else {
            if (addToResults(root) > 0) {
                return;

            } else {
                for (Node node : root.childNodes()) {
                    extractData(node);
                }
            }
        }

        return;
    }


    /**
     * Decide whether a node's children are the _results to output. If they are,
     * they will be put in an arraylist. And the arraylist will be one element
     * of the class' member results.
     * @param node whose children are examined.
     * @return the number of results to output. If it is 0, this node's children
     * are not the results users are interested in.
     */
    private int addToResults(Node node) {
        ArrayList<Node> outputList = new ArrayList<Node>();
        int childNodeSize = node.childNodeSize();

        boolean[] voteResult;

        System.out.println(node.numOffSpring + ":");

        voteResult = vote(node);
        for(int i = 0; i < childNodeSize; i++) {
            if(voteResult[i]) {
                outputList.add(node.childNode(i));
            }
        }

        if (outputList.isEmpty()) {
            return 0;
        } else {
            _results.add(outputList);
            return outputList.size();
        }

    }


    /**
     * The node's children will vote for each other based on _proximity. If two
     * children are close enough, they will give each other one vote. If one c-
     * hild get enough votes, its vote result is true. That means it is one of
     * the possible results which the user is interested in.
     * @param node
     * @return the vote result for each child.
     */
    private boolean[] vote(Node node) {
        float voteThreshold = 0.6f; //TODO: modularize

        /* Input: */
        int childNodeSize = node.childNodeSize();

        /* Output: */
        int[] voteCount = new int[childNodeSize];
        boolean[] voteResult = new boolean[childNodeSize];

        /* Vote for each node */
        for(int i = 0; i < childNodeSize; i++) {
            for(int j = i + 1; j < childNodeSize; j++) {
                /* Check for null String */
                if (node.childNode(i).APTEDTreeStructure == null || node.childNode(j).APTEDTreeStructure == null) {
                    continue;
                }

                /* Compute TED */
                apted.node.Node<StringNodeData> t1 = _APTEDParser.fromString(node.childNode(i).APTEDTreeStructure);
                apted.node.Node<StringNodeData> t2 = _APTEDParser.fromString(node.childNode(j).APTEDTreeStructure);

                /* Judge if TED exceed threshold */
                float TEDthreshold = (node.childNode(i).numOffSpring + node.childNode(j).numOffSpring) / 2
                        * (1 - _proximity);
                //System.out.println("i"+i+"j"+j+"tmp"+tmp+"distance"+_apted.computeEditDistance(t1, t2));
                if (TEDthreshold >= _apted.computeEditDistance(t1, t2)) {
                    voteCount[i]++;
                    voteCount[j]++;
                }
            }
        }

        /* Collect voting results */
        for(int i = 0; i < childNodeSize; i++) {
            System.out.print(voteCount[i]);
            if (voteCount[i] >= voteThreshold * childNodeSize) {
                voteResult[i] = true;
            } else {
                voteResult[i] = false;
            }
        }

        return voteResult;
    }

    /**
     * Filter the results given the minimum result size
     * @param minResultSize
     * @return
     */
    public boolean filterByNumber(int minResultSize) {
        if (minResultSize < 1) {
            throw new IllegalArgumentException("Min result size should be larger than 0!");
        }

        for(int i = 0; i < _results.size(); i++) {
            if(_results.get(i).size() < minResultSize) {
                _results.remove(i);
                i--;
            }
        }
        return true;
    }

}
