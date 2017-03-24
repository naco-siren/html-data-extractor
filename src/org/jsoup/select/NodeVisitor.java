package org.jsoup.select;

import org.jsoup.nodes.Node;

/**
 * Node visitor interface. Provide an implementing class to {@link NodeTraversor} to iterate through nodes.
 * <p>
 * This interface provides two methods, {@code head} and {@code tail}. The head method is called when the apted.node is first
 * seen, and the tail method when all of the apted.node's children have been visited. As an example, head can be used to
 * create a start tag for a apted.node, and tail to create the end tag.
 * </p>
 */
public interface NodeVisitor {
    /**
     * Callback for when a apted.node is first visited.
     *
     * @param node the apted.node being visited.
     * @param depth the depth of the apted.node, relative to the root apted.node. E.g., the root apted.node has depth 0, and a child apted.node
     * of that will have depth 1.
     */
    void head(Node node, int depth);

    /**
     * Callback for when a apted.node is last visited, after all of its descendants have been visited.
     *
     * @param node the apted.node being visited.
     * @param depth the depth of the apted.node, relative to the root apted.node. E.g., the root apted.node has depth 0, and a child apted.node
     * of that will have depth 1.
     */
    void tail(Node node, int depth);
}
