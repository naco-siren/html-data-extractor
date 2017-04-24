package gokurakujoudo.dom_tree_helpers;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.NodeVisitor;

/**
 * Created by nacos on 4/23/2017.
 */
public class AElementVisitor implements NodeVisitor {

    @Override
    public void head(Node node, int depth) {

    }

    @Override
    public void tail(Node node, int depth) {

        /* Check if the node is not an <a> element */
        if (node instanceof Element == false)
            return;
        Element element = (Element) node;
        if (element.tagName().equals("a") == false)
            return;


        /* Unwrap by default  */
        boolean shouldUnwrap = true;


        /* Fetch its previous sibling and next sibling */
        Element prevSib = element.previousElementSibling();
        Element nextSib = element.nextElementSibling();

        if (prevSib == null && nextSib == null) {
            /* If it is the sole child element of its parent */
            shouldUnwrap = true;

        } else {
            /* If its previous sibling is an element with a positive margin-right */
            int prevMarginR = 0;
            if (prevSib != null) {
                String prevMarginRight = prevSib.attr("margin-right");
                prevMarginR = prevMarginRight != null && prevMarginRight.length() > 0 ? Integer.parseInt(prevMarginRight.split("px")[0]) : 0;
            }

            /* If its next sibling is an element with a positive margin-left */
            int nextMarginL = 0;
            if (nextSib != null) {
                String nextMarginLeft = nextSib.attr("margin-left");
                nextMarginL = nextMarginLeft != null && nextMarginLeft.length() > 0 ? Integer.parseInt(nextMarginLeft.split("px")[0]) : 0;
            }

            if (prevMarginR > 0 || nextMarginL > 0)
                shouldUnwrap = false;
        }


        /* Perform unwrapping */
        if (shouldUnwrap == true)
            element.unwrap();
    }
}
