package gokurakujoudo.dom_tree_helpers;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeVisitor;

import java.util.ArrayList;

/**
 * Created by nacos on 4/23/2017.
 */
public class AElementVisitor implements NodeVisitor {

    /**
     * When into the
     * @param node the node being visited.
     * @param depth the depth of the node, relative to the root node. E.g., the root node has depth 0, and a child node
     */
    @Override
    public void head(Node node, int depth) {
        return;
    }


    /**
     * Unwrap the element's
     * @param node the apted.node being visited.
     * @param depth the depth of the apted.node, relative to the root apted.node. E.g., the root apted.node has depth 0, and a child apted.node
     */
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

    public boolean shouldUnwrap(Node node, int depth){

        /* Check if the node is not an <a> element */
        if (node instanceof Element == false)
            return false;
        Element element = (Element) node;
        if (element.tagName().equals("a") == false)
            return false;


        /* Fetch its previous sibling and next sibling node */
        Node prevSib = element.previousSibling();
        Node nextSib = element.nextSibling();

        /* If it's the only child */
        if (prevSib == null && nextSib == null)
            return true;

        /* If its previous and next sibling nodes are text nodes */
        TextNode prevSibTextNode = prevSib == null ? null : (prevSib instanceof TextNode ? (TextNode) prevSib : null);
        TextNode nextSibTextNode = nextSib == null ? null : (nextSib instanceof TextNode ? (TextNode) nextSib : null);
        /* And if they are blanks */
        boolean prevTextIsBlank = prevSibTextNode != null && StringUtils.isBlank(prevSibTextNode.text());
        boolean nextTextIsBlank = nextSibTextNode != null && StringUtils.isBlank(nextSibTextNode.text());
        if (prevTextIsBlank || nextTextIsBlank)
            return false;



        /* Fetch its previous sibling element and next sibling element */
//        Element prevSibEle = element.previousElementSibling();
//        Element nextSibEle = element.nextElementSibling();
        Element prevSibEle = prevSib instanceof Element ? (Element) prevSib : null;
        Element nextSibEle = nextSib instanceof Element ? (Element) nextSib : null;

        /* If it is the sole child element of its parent */
        if (prevSibEle == null && nextSibEle == null)
            return true;


        /* If its has a positive distance (padding + margin) to the previous element sibling */
        int prevDistance = px2int(element.attr("padding-left")) + px2int(element.attr("margin-left"));
        if (prevSibEle != null)
            prevDistance += px2int(prevSibEle.attr("margin-right")) + px2int(prevSibEle.attr("padding-right"));

        /* If its has a positive distance (padding + margin) to the previous element sibling */
        int nextDistance = px2int(element.attr("padding-right")) + px2int(element.attr("margin-right"));
        if (nextSibEle != null)
            nextDistance += px2int(nextSibEle.attr("margin-left")) + px2int(nextSibEle.attr("padding-left"));


        if (prevDistance > 0 || nextDistance > 0) {
            return false;
        } else {
            return true;
        }

    }

    private int px2int(String px) {
        if (px == null)
            return 0;

        String[] parts = px.split("px");
        if (parts.length == 0)
            return 0;

        return Integer.parseInt(parts[0]);
    }
}
