package gokurakujoudo.dom_tree_helpers;

import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by haosun on 3/25/17.
 */
public class DomTreeCleaner {
    /* Input: */
    private Element _root;

    /**
     * Constructor
     * @param root: the root of the DOM tree to clean
     */
    public DomTreeCleaner(Element root){
        _root = root;
    }

    /**
     * Perform DOM tree cleaning
     * @param tagNamesToRemove
     */
    public int clean(String[] tagNamesToRemove) {
        try {
            /* Remove elements with given tag names */
            Elements elementsToRemove = new Elements();
            for (String removeTagName : tagNamesToRemove) {
                elementsToRemove.addAll(_root.getElementsByTag(removeTagName));
            }
            elementsToRemove.remove();

            /* Remove blank lines and comments */
            ArrayList<Node> nodesToDelete = new ArrayList<>();
            Node cursor = _root;
            int depth = 0;
            while (cursor != null) {
                if (cursor.childNodeSize() > 0) {
                    cursor = cursor.childNode(0);
                    depth++;
                } else {
                    while (cursor.nextSibling() == null && depth > 0) {
                        tail(cursor, nodesToDelete);
                        cursor = cursor.parentNode();
                        depth--;
                    }
                    tail(cursor, nodesToDelete);
                    if (cursor == _root)
                        break;
                    cursor = cursor.nextSibling();
                }
            }
            for (Node node : nodesToDelete) {
                node.remove();
            }

            return 0;

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void tail(Node node, ArrayList<Node> nodeArrayList){
        if(node instanceof Comment) nodeArrayList.add(node);

        if(node instanceof TextNode && ((TextNode) node).isBlank()) nodeArrayList.add(node);
    }

//    private static String cleanHtmlFragment(String htmlFragment, String attributesToRemove) {
//        return htmlFragment.replaceAll("\\s+(?:" + attributesToRemove + ")\\s*=\\s*\"[^\"]*\"","");
//    }
}
