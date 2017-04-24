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

        String tagName = element.tagName();
        if (tagName.equals("a") == false && tagName.equals("span") == false)
            return false;


        /* Fetch its previous sibling and next sibling node */
        Node prevSib = element.previousSibling();
        Node nextSib = element.nextSibling();

        /* If it's the only child */
        if (prevSib == null && nextSib == null)
            return true;


        /* If it has an emphasizing style */
//        if (element.attr("font-style").equals("normal") == false || element.attr("font-weight").equals("normal") == false)
//            return false;


        /* Check if it has blank to its previous sibling */
        boolean hasBlankToPrev = false;
        if (prevSib == null) {
            /* If previous sibling not exist */
            hasBlankToPrev = false;
            
        } else if (prevSib instanceof TextNode) {
            TextNode prevTextSib = (TextNode) prevSib;
            
            /* If previous sibling is a text node with blank text */
            hasBlankToPrev = StringUtils.isBlank(prevTextSib.text());
            
        } else if (prevSib instanceof Element) {
            Element prevEleSib = (Element) prevSib;
            
            /* If previous sibling us an element with right padding + margin */
            int prevDistance = px2int(element.attr("padding-left")) + px2int(element.attr("margin-left"));
            prevDistance += px2int(prevEleSib.attr("margin-right")) + px2int(prevEleSib.attr("padding-right"));

            hasBlankToPrev = prevDistance > 0;
        }

        /* Check if it has blank to its next sibling */
        boolean hasBlankToNext = false;
        if (nextSib == null) {
            /* If Nextious sibling not exist */
            hasBlankToNext = false;

        } else if (nextSib instanceof TextNode) {
            TextNode NextTextSib = (TextNode) nextSib;
            
            /* If Nextious sibling is a text node with blank text */
            hasBlankToNext = StringUtils.isBlank(NextTextSib.text());

        } else if (nextSib instanceof Element) {
            Element NextEleSib = (Element) nextSib;
            
            /* If Nextious sibling us an element with right padding + margin */
            int NextDistance = px2int(element.attr("padding-right")) + px2int(element.attr("margin-right"));
            NextDistance += px2int(NextEleSib.attr("margin-left")) + px2int(NextEleSib.attr("padding-left"));

            hasBlankToNext = NextDistance > 0;
        }


        if (hasBlankToPrev || hasBlankToNext)
            return false;
        else
            return true;

    }

    private int px2int(String px) {
        if (px == null)
            return 0;

        String[] parts = px.split("px");
        if (parts.length == 0)
            return 0;

        try {
            int value = Integer.parseInt(parts[0]);
            return value;

        } catch (Exception e) {
            return 0;
        }
    }
}
