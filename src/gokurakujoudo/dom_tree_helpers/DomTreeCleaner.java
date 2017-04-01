package gokurakujoudo.dom_tree_helpers;

import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by haosun on 3/25/17.
 */
public class DomTreeCleaner {
    private final String[] TAG_NAMES_TO_REMOVE = new String[]{"meta", "script", "style", "base", "video", "audio"};
    //private final String[] ATTRIBUTE_KETS_TO_REMOVE = new String[]{"href", "id", "class", "style"};

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
     */
    public int clean() {

        try {
            /* Remove elements with given tag names */
            Elements elementsToRemove = new Elements();
            for (String removeTagName : TAG_NAMES_TO_REMOVE) {
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
                        cacheVoidNode(cursor, nodesToDelete);
                        cursor = cursor.parentNode();
                        depth--;
                    }
                    cacheVoidNode(cursor, nodesToDelete);
                    if (cursor == _root)
                        break;
                    cursor = cursor.nextSibling();
                }
            }
            for (Node node : nodesToDelete) {
                node.remove();
            }


            /* Remove the attributes with given keys */
            Elements elements = _root.getAllElements();
            for (Element ele : elements) {
                List<Attribute> attrs = ele.attributes().asList();
                for (Attribute attr : attrs) {
                    ele.removeAttr(attr.getKey());
                }
            }

            return 0;

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void cacheVoidNode(Node node, ArrayList<Node> nodeArrayList){
        if(node instanceof Comment) nodeArrayList.add(node);

        if(node instanceof TextNode && ((TextNode) node).isBlank()) nodeArrayList.add(node);
        
        //TODO: Experimental
        //if (node instanceof Element && node.childNodeSize() == 0) nodeArrayList.add(node);
    }

//    private static String cleanHtmlFragment(String htmlFragment, String attributesToRemove) {
//        return htmlFragment.replaceAll("\\s+(?:" + attributesToRemove + ")\\s*=\\s*\"[^\"]*\"","");
//    }
}
