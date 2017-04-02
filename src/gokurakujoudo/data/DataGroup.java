package gokurakujoudo.data;

import org.json.XML;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

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

    /* TODO: get data's HTML/JSON */
    public ArrayList<String> getDataHTMLs(){
        ArrayList<String> results = new ArrayList<>();

        for (Node node : _data) {
            String outerHTML = node.outerHtml();
            results.add(outerHTML);

            String json = XML.toJSONObject(outerHTML).toString();
            System.out.println(json);
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
        if (node instanceof Element == false) {
            return 0;
        }
        Element element = (Element) node;


        try {
            /* Remove elements with given tag names */
            Elements elementsToRemove = new Elements();
            for (String removeTagName : TAG_NAMES_TO_REMOVE) {
                elementsToRemove.addAll(element.getElementsByTag(removeTagName));
            }
            elementsToRemove.remove();

            /* Remove elements with no children */
            ArrayList<Node> nodesToDelete = new ArrayList<>();
            Node cursor = element;
            int depth = 0;
            while (cursor != null) {
                if (cursor.childNodeSize() > 0) {
                    cursor = cursor.childNode(0);
                    depth++;
                } else {
                    while (cursor.nextSibling() == null && depth > 0) {
                        cacheLonelyNodes(cursor, nodesToDelete);
                        cursor = cursor.parentNode();
                        depth--;
                    }
                    cacheLonelyNodes(cursor, nodesToDelete);
                    if (cursor == element)
                        break;
                    cursor = cursor.nextSibling();
                }
            }
            for (Node n : nodesToDelete) {
                n.remove();
            }

            /* Unwrap <font>, <strong>, <em>, <b>, <i>, <u>, <s>, <br>, <sup>, <sub> */
            for (String tagName : TAG_NAMES_TO_UNWRAP) {
                element.select(tagName).unwrap();
            }
            element.select("a").unwrap();


            return 0;

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    //TODO: Experimental
    private void cacheLonelyNodes(Node node, ArrayList<Node> nodeArrayList){
        if (node instanceof Element && node.childNodeSize() == 0) nodeArrayList.add(node);
    }
}
