package gokurakujoudo.data;

import gokurakujoudo.dom_tree_helpers.DomTreeCleaner;
import gokurakujoudo.dom_tree_helpers.FinalizeDOMTreeVisitor;
import org.json.JSONObject;
import org.json.XML;
import org.jsoup.nodes.*;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

import java.util.ArrayList;

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

            try {
                JSONObject jsonObject = XML.toJSONObject(outerHTML);
                String jsonString = jsonObject.toString();
                results.add(jsonString);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println(outerHTML);
            }

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

        /* Remove attributes */
        if (DomTreeCleaner.removeDOMTreeAttributes(element) != 0)
            return -1;

        /* Finalize the data for output */
        if (finalizeData(element) != 0)
            return -2;

        return 0;

    }

    /**
     * Finalize the data for output by removing nodes with blank text and unwrapping single children
     * @param root
     * @return 0 on success
     */
    private int finalizeData(Element root){
        /* Remove nodes with blank text */
        try {
            FinalizeDOMTreeVisitor finalizeDOMTreeVisitor = new FinalizeDOMTreeVisitor();
            NodeTraversor nodeTraversor = new NodeTraversor(finalizeDOMTreeVisitor);
            nodeTraversor.traverse(root);

            return 0;

        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }
}
