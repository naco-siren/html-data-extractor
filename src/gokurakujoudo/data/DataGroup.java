package gokurakujoudo.data;

import gokurakujoudo.dom_tree_helpers.DomTreeCleaner;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.json.XML;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

import java.util.ArrayList;

import static gokurakujoudo.utils.InjectionUtils.*;

/**
 * Created by naco_siren on 3/29/17.
 */
public class DataGroup implements Comparable<DataGroup> {
    /* Data */
    public ArrayList<Node> _data;
    public int size(){
        return _data.size();
    }

    /*  */
    public double _significance;

    /**
     * Constructor
     */
    public DataGroup(){
        this._data = new ArrayList<>();
        this._significance = 0;
    }

    /**
     * Comparator for ranking
     */
    @Override
    public int compareTo(DataGroup other) {
        return (int) (this._significance - other._significance);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Data size: " + _data.size() + ", Significance: " + _significance);
        return stringBuilder.toString();
    }

    /**
     * Get data in HTML or JSON format
     * @return a list of HTML or JSON Strings
     */
    public ArrayList<String> getHTMLs(){
        ArrayList<String> results = new ArrayList<>();

        for (Node node : _data) {
            String outerHTML = node.outerHtml();
            results.add(outerHTML);
        }

        return results;
    }
    public ArrayList<String> getJSONs(){
        ArrayList<String> results = new ArrayList<>();

        for (Node node : _data) {
            String outerHTML = node.outerHtml();

            JSONObject jsonObject = XML.toJSONObject(outerHTML);
            String jsonString = jsonObject.toString();

            results.add(jsonString);
        }
        return results;
    }


    /**
     * Clean the extracted data
     * @return
     */
    public int clean() {
        int retval = 0;
        for (Node node : _data) {
            retval += cleanDataNode(node);
        }
        return retval;
    }
    private final String[] TAG_NAMES_TO_REMOVE = new String[]{"br"};
    private final String[] TAG_NAMES_TO_UNWRAP = new String[]{"font", "strong", "em", "b", "i", "u", "s", "br", "sup", "sub"};
    private int cleanDataNode(Node node){
        /* Check if Node is not instance of Element */
        if (node instanceof Element == false)
            return 0;
        Element element = (Element) node;

        /* Unwrap single-child elements */
        try {
            SingleChildUnwrappingVisitor singleChildUnwrappingVisitor = new SingleChildUnwrappingVisitor();
            NodeTraversor nodeTraversor = new NodeTraversor(singleChildUnwrappingVisitor);
            nodeTraversor.traverse(element);

        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return -1;
        }

        /* Remove attributes */
        if (DomTreeCleaner.removeDOMTreeAttributes(element) == 0)
            return 0;
        else
            return -2;
    }

    /**
     * Used for unwrapping single-child elements
     */
    class SingleChildUnwrappingVisitor implements NodeVisitor {
        @Override
        public void head(Node node, int depth) {

        }

        @Override
        public void tail(Node node, int depth) {
            /* Unwrap all the children demands unwrapping */
            ArrayList<Node> childrenDemandsUnwrapping = new ArrayList<>();
            for (Node childNode : node.childNodes()) {
                if (childNode.needsUnwrapping == true)
                    childrenDemandsUnwrapping.add(childNode);
            }
            for (Node childNode : childrenDemandsUnwrapping)
                childNode.unwrap();

            /* If current node needs unwrapping, leave a mark */
            boolean shouldUnwrap = shouldUnwrap(node, depth);
            node.needsUnwrapping = shouldUnwrap;

            return;
        }

        private boolean shouldUnwrap(Node node, int depth){
            /* Check if the node is not an <a> element */
            if (node instanceof Element == false)
                return false;
            Element element = (Element) node;

            /* Fetch its previous sibling and next sibling node */
            Element prevSibEle = element.previousElementSibling();
            Element nextSibEle = element.nextElementSibling();

            /* If it's the only child */
            if (prevSibEle == null && nextSibEle == null)
                return true;
            else
                return false;
        }
    }


}
