package gokurakujoudo.dom_tree_helpers;

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

        /* If its previous and next sibling nodes are null or textnodes */
        boolean prevSibIsTextNode = prevSib == null || prevSib instanceof TextNode;
        boolean nextSibIsTextNode = nextSib == null || prevSib instanceof TextNode;
        if (prevSibIsTextNode && nextSibIsTextNode)
            return true;



        /* If  */

        Element prevSibEle = element.previousElementSibling();
        Element nextSibEle = element.nextElementSibling();

        /* If it is the sole child element of its parent */
        if (prevSibEle == null && nextSibEle == null) {
            return true;
        }


        /* If its previous sibling is an element with a positive margin-right */
        int prevMarginR = 0;
        if (prevSibEle != null) {
            String prevMarginRight = prevSibEle.attr("margin-right");
            prevMarginR = prevMarginRight != null && prevMarginRight.length() > 0 ? Integer.parseInt(prevMarginRight.split("px")[0]) : 0;
        }

        /* If its next sibling is an element with a positive margin-left */
        int nextMarginL = 0;
        if (nextSibEle != null) {
            String nextMarginLeft = nextSibEle.attr("margin-left");
            nextMarginL = nextMarginLeft != null && nextMarginLeft.length() > 0 ? Integer.parseInt(nextMarginLeft.split("px")[0]) : 0;
        }

        if (prevMarginR > 0 || nextMarginL > 0) {
            return false;
        } else {
            return true;
        }

    }
}
