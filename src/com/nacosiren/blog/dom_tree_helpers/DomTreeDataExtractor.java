package com.nacosiren.blog.dom_tree_helpers;

import apted.costmodel.StringUnitCostModel;
import apted.distance.APTED;
import apted.node.StringNodeData;
import apted.parser.BracketStringInputParser;
import com.nacosiren.blog.data.DataGroup;
import com.nacosiren.blog.data.DataGroups;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.NodeTraversor;

import java.util.ArrayList;

/**HTML extractor is used to extract data which users are interested
 * in form a HTML web page.
 * The input is a URL link or a HTML web page document. The data w-
 * ill be extracted and exported into a database-like schema.
 * Created by haosun on 3/27/17.
 */
public class DomTreeDataExtractor {

    /* Input: */
    private Node _root;

    /* Params: Proximity */
    private float _proximity = 0.6f;
    public void setProximity(float proximity) {
        this._proximity = proximity;
    }
    public double getProximity() {
        return _proximity;
    }
    
    /* Params: TED */
    private boolean _considerTED = true;
    public void setConsiderTED(boolean considerTED) {
        this._considerTED = considerTED;
    }
    public boolean getConsiderTED(){
        return _considerTED;
    }

    /* Params: Area */
    private boolean _considerArea = true;
    public void setConsiderArea(boolean considerArea) {
        this._considerArea = considerArea;
    }
    public boolean getConsiderArea(){
        return _considerArea;
    }
    
    /* Output: Results */
    private DataGroups _results = new DataGroups();
    public DataGroups getResults(){
        /* Check if results null */
        if (_results == null)
            return null;
        else
            return _results;
    }

    /* Tools:  */
    private BracketStringInputParser _APTEDParser = new BracketStringInputParser();
    private APTED<StringUnitCostModel, StringNodeData> _apted = new APTED<>(new StringUnitCostModel());

    /**
     * Constructor
     * @param root: the root of the input DOM tree
     */
    public DomTreeDataExtractor(Node root) {
        this._root = root;
    }


    /**
     * Traverse the tree (pre-order) to extract _results from the HTML doc.
     * Call method on root, if its children are hit _results, move to its
     * Sibling. If not, call method on all its child recursively one by one.
     */
    public int extractData(){

//        /* Unwrap <a> elements */
//        UnwrapANodeVisitor unwrapANodeVisitor = new UnwrapANodeVisitor();
//        NodeTraversor unwrapANodeTraversor = new NodeTraversor(unwrapANodeVisitor);
//        unwrapANodeTraversor.traverse(_root);

        /* Construct APTED structure for each node */
        APTEDNodeVisitor aptedNodeVisitor = new APTEDNodeVisitor();
        NodeTraversor APTEDNodeTraversor = new NodeTraversor(aptedNodeVisitor);
        APTEDNodeTraversor.traverse(_root);

        /* Recursively extract data using pre-order traversing */
        extractDataRecursive(_root);

        return 0;
    }
    /**
     * Recursive part of extractData()
     * @param root the root node of the DOM representation of the HTML doc.
     */
    private void extractDataRecursive(Node root) {
        int childNodeSize = root.childNodeSize();

        if (childNodeSize <= 0) {
            return;

        } else if (childNodeSize == 1){
            extractDataRecursive(root.childNode(0));

        } else {
            if (addToResults(root) > 0) {
                return;

            } else {
                for (Node node : root.childNodes()) {
                    extractDataRecursive(node);
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

        DataGroup dataGroup = new DataGroup();
        dataGroup._data = new ArrayList<>();
        ArrayList<Node> outputList = dataGroup._data;

        int childNodeSize = node.childNodeSize();
        //System.out.println(node.numOffsprings + ":");
        boolean[] voteResult = vote(node);
        for(int i = 0; i < childNodeSize; i++) {
            if(voteResult[i]) {
                outputList.add(node.childNode(i));
            }
        }

        if (outputList.isEmpty()) {
            return 0;
        } else {
            _results.add(dataGroup);
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
        int voteThreshold = 50;//in percent
        int areaVoteWeight = 40;
        int TEDVoteWeight = 60;

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

                /* Comprehensive voting, considering area difference and APTED. Different weight values are assigned to these two*/

                /* Area similarity*/
                if (_considerArea == false) {
                    //pass
                } else if (node.childNode(i) instanceof Element && node.childNode(j) instanceof Element) {
                    Element ele_i = (Element) node.childNode(i);
                    Element ele_j = (Element) node.childNode(j);
                    String area_str_i = ele_i.attr("area");
                    String area_str_j = ele_j.attr("area");
                    if (area_str_i == "" || area_str_j == "") {
                        voteCount[i] += areaVoteWeight;
                        voteCount[j] += areaVoteWeight;
                    } else {
                        /* If the bigger area of the two nodes is within the 1.35 times of the other one, the two nodes are similar enough*/
                        if (Math.max(Integer.parseInt(area_str_i), Integer.parseInt(area_str_j))
                                <= 1.35 * Math.min(Integer.parseInt(area_str_i), Integer.parseInt(area_str_j))) {
                            voteCount[i] += areaVoteWeight;
                            voteCount[j] += areaVoteWeight;
                        }
                    }
                }

                /* Compute TED */
                if (_considerTED == false) {
                    // Pass
                } else {
                    apted.node.Node<StringNodeData> t1 = _APTEDParser.fromString(node.childNode(i).APTEDTreeStructure);
                    apted.node.Node<StringNodeData> t2 = _APTEDParser.fromString(node.childNode(j).APTEDTreeStructure);

                    /* Judge if TED exceed threshold */
                    float TEDthreshold = (node.childNode(i).numOffsprings + node.childNode(j).numOffsprings) / 2
                            * (1 - _proximity);
                    //System.out.println("i"+i+"j"+j+"tmp"+tmp+"distance"+_apted.computeEditDistance(t1, t2));
                    if (TEDthreshold >= _apted.computeEditDistance(t1, t2)) {
                        voteCount[i] += TEDVoteWeight;
                        voteCount[j] += TEDVoteWeight;
                    }
                }
            }
        }

        /* Collect voting results */
        int consideredArea = (_considerArea == true) ? 1 : 0;
        int consideredTED = (_considerTED == true) ? 1: 0;
        for(int i = 0; i < childNodeSize; i++) {
            //System.out.print(voteCount[i]);
            if ((voteCount[i] * 100) >= voteThreshold * childNodeSize * (consideredArea * areaVoteWeight + consideredTED * TEDVoteWeight)) {
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
    public void filterByMinResultSize(int minResultSize) throws Exception {
        /* Check if argument illegal */
        if (minResultSize < 1) {
            throw new IllegalArgumentException("Min result size should be larger than 0!");
        }

        /* Check if results null */
        if (_results == null) {
            throw new Exception("Must run extractData() first before filtering!");
        }

        /* Remove DataGroups below minimum result size */
        for(int i = 0; i < _results.size(); i++) {
            if(_results.get(i)._data.size() < minResultSize) {
                _results.remove(i);
                i--;
            }
        }
    }

}
