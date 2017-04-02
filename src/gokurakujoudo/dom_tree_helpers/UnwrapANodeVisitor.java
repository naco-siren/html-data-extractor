package gokurakujoudo.dom_tree_helpers;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeVisitor;

/**
 * Created by naco_siren on 4/1/17.
 */
public class UnwrapANodeVisitor implements NodeVisitor {
    @Override
    public void head(Node node, int depth) {
//        if (node instanceof Element) {
//            // System.out.println("Enter Element: " + ((Element) node).tag() + ", " + depth);
//        }
//        else {
//            // System.out.println("Enter Text: " + ((TextNode) node).text() + ", " + depth);
//        }
    }

    @Override
    public void tail(Node node, int depth) {

        /* Check if Node is not <a>*/
        if (node instanceof Element == false) {
            return;
        }
        Element element = (Element) node;
        if (element.tagName().equals("a") == false){
            return;
        }


//        /* Check if parent's sole child */
//        Element parent = element.parent();
//        if (parent.childNodeSize() == 1) {
//            element.unwrap();
//            return;
//        }

        /* Check if its sibling is textnode */
        Node prev = node.previousSibling();
        Node next = node.nextSibling();
        if (prev instanceof TextNode || next instanceof TextNode) {
            element.unwrap();
            return;
        }

    }
}
