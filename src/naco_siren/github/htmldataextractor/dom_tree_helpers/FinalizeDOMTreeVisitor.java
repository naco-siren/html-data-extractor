package naco_siren.github.htmldataextractor.dom_tree_helpers;

import org.apache.http.util.TextUtils;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeVisitor;

import java.util.ArrayList;

/**
 * Created by nacos on 4/29/2017.
 */
public class FinalizeDOMTreeVisitor implements NodeVisitor {
    @Override
    public void head(Node node, int depth) {

    }

    @Override
    public void tail(Node node, int depth) {
            /* Remove all the children demands removing */
        ArrayList<Node> childrenDemandsRemoving = new ArrayList<>();
        for (Node childNode : node.childNodes()) {
            if (childNode.needsRemoving == true)
                childrenDemandsRemoving.add(childNode);
        }
        for (Node childNode : childrenDemandsRemoving)
            childNode.remove();

            /* If current node needs removing, leave a mark */
        boolean shouldRemove = shouldRemove(node, depth);
        node.needsRemoving = shouldRemove;

        if (shouldRemove == true)
            return;

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
    }

    private boolean shouldUnwrap(Node node, int depth){
            /* Check if the node is not an <a> element */
        if (node instanceof Element == false)
            return false;
        Element element = (Element) node;

            /* Fetch its previous sibling and next sibling node */
        Element prevSibEle = element.previousElementSibling();
        Element nextSibEle = element.nextElementSibling();

            /* If it's the only child */
        if (prevSibEle == null && nextSibEle == null)
            return true;
        else
            return false;
    }

    private boolean shouldRemove(Node node, int depth) {
        String text = null;
        if (node instanceof TextNode) {
            text = ((TextNode) node).text();
        } else if (node instanceof Element) {
            text = ((Element) node).text();
        }

        if (TextUtils.isBlank(text))
            return true;
        else
            return false;
    }
}