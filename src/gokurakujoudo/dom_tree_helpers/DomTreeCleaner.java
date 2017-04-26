package gokurakujoudo.dom_tree_helpers;

import gokurakujoudo.utils.InjectionUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeTraversor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by haosun on 3/25/17.
 */
public class DomTreeCleaner {
    private static final String[] TAG_NAMES_TO_REMOVE = new String[]{"meta", "script", "style", "base", "video", "audio"};
    private static final String[] TAG_NAMES_TO_UNWRAP = new String[]{"font", "strong", "em", "b", "i", "u", "s", "br", "sup", "sub"};
    //private final String[] ATTRIBUTE_KEYS_TO_REMOVE = new String[]{"href", "id", "class", "style"};

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
     * Perform comprehensive DOM tree cleaning
     * @return 0 on success
     */
    public int clean() {
        if (trim() != 0)
            return -1;
        if (smartUnwrap() != 0)
            return -2;

        return 0;
    }


    /**
     * Trim out useless elements
     * @return 0 on success
     */
    private int trim(){
        return trimDOMTree(_root);
    }
    public static int trimDOMTree(Element root) {
        try {
            /* Remove elements with given tag names */
            Elements elementsToRemove = new Elements();
            for (String removeTagName : TAG_NAMES_TO_REMOVE) {
                elementsToRemove.addAll(root.getElementsByTag(removeTagName));
            }
            elementsToRemove.remove();

            /* Remove elements that are not visible */
            // In style
            elementsToRemove = root.getElementsByAttributeValueContaining("style", "display:none");
            elementsToRemove.remove();
            // In dimension
            elementsToRemove = root.getElementsByAttributeValue(InjectionUtils.AREA, "0");
            elementsToRemove.remove();

            /* Remove blank lines and comments */
            ArrayList<Node> nodesToDelete = new ArrayList<>();
            Node cursor = root;
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
                    if (cursor == root)
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
    private static void cacheVoidNode(Node node, ArrayList<Node> nodeArrayList){
        if(node instanceof Comment) nodeArrayList.add(node);

        if(node instanceof TextNode && ((TextNode) node).isBlank()) nodeArrayList.add(node);

        //TODO: Experimental
        //if (node instanceof Element && node.childNodeSize() == 0) nodeArrayList.add(node);
    }


    /**
     * Smartly unwrap the <a> elements based on context
     * @return 0 on success
     */
    private int smartUnwrap() {
        return smartUnwrapDOMTree(_root);
    }
    public static int smartUnwrapDOMTree (Element root) {
        try {
            /* Unwrap <font>, <strong>, <em>, <b>, <i>, <u>, <s>, <br>, <sup>, <sub> */
            for (String tagName : TAG_NAMES_TO_UNWRAP) {
                root.select(tagName).unwrap();
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return -1;
        }

        try {
            /* Unwrap <a> elements according to the context */
            AElementVisitor aElementVisitor = new AElementVisitor();
            NodeTraversor unwrappingTraversor = new NodeTraversor(aElementVisitor);
            unwrappingTraversor.traverse(root);
            return 0;

        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return -2;
        }
    }
    public static String smartUnwrapHTML(String html){
        Document document = Jsoup.parse(html);
        Element body = document.body();
        smartUnwrapDOMTree(body);
        return body.outerHtml();
    }


    /**
     * Remove all attributes in a DOM tree
     * @return 0 on success
     */
//    private int removeAttributes(){
//        return removeDOMTreeAttributes(_root);
//    }
    public static int removeDOMTreeAttributes(Element root){
        try {
            /* Remove the attributes with given keys */
            Elements elements = root.getAllElements();
            for (Element ele : elements) {
                List<Attribute> attrs = ele.attributes().asList();
                for (Attribute attr : attrs) {
                    ele.removeAttr(attr.getKey());
                }
            }
            return 0;

        } catch (Exception e) {
            return -1;
        }
    }

//    private static String cleanHtmlFragment(String htmlFragment, String attributesToRemove) {
//        return htmlFragment.replaceAll("\\s+(?:" + attributesToRemove + ")\\s*=\\s*\"[^\"]*\"","");
//    }
}
