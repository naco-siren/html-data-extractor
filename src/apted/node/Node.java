/* MIT License
 *
 * Copyright (c) 2017 Mateusz Pawlik
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package apted.node;

import java.util.Vector;

/**
 * This is a recursive representation of an ordered tree. Each apted.node stores a
 * vector of pointers to its children. The order of children is significant and
 * must be observed while implmeneting a custom input apted.parser.
 *
 * @param <D> the type of apted.node data (apted.node label).
 */
public class Node<D> {

  /**
   * Information associated to and stored at each apted.node. This can be anything
   * and depends on the application, for example, string label, key-value pair,
   * vector of values, etc.
   */
  private D nodeData;

  /**
   * Array of pointers to this apted.node's children. The order of children is
   * significant due to the definition of ordered trees.
   */
  private Vector<Node<D>> children;

  /**
   * Constructs a new apted.node with the passed apted.node data and an empty vector of
   * children.
   *
   * @param nodeData instance of apted.node data (apted.node label).
   */
  public Node(D nodeData) {
    this.children = new Vector<Node<D>>();
    setNodeData(nodeData);
  }

  /**
   * Counts the number of nodes in a tree rooted at this apted.node.
   *
   * <p>This method runs in linear time in the tree size.
   *
   * @return number of nodes in the tree rooted at this apted.node.
   */
  public int getNodeCount() {
      int sum = 1;
      for(Node<D> child : getChildren()) {
        sum += child.getNodeCount();
      }
      return sum;
  }

  /**
   * Adds a new child at the end of children vector. The added child will be
   * the last child of this apted.node.
   *
   * @param c child apted.node to add.
   */
  public void addChild(Node c) {
    this.children.add(c);
  }

  /**
   * Returns a string representation of the tree in bracket notation.
   *
   * <p>IMPORTANT: Works only for nodes storing {@link apted.node.StringNodeData}
   * due to using {@link apted.node.StringNodeData#getLabel()}.
   *
   * @return tree in bracket notation.
   */
  public String toString() {
    String res = (new StringBuilder("{")).append(((StringNodeData)getNodeData()).getLabel()).toString();
    for(Node<D> child : getChildren()) {
      res = (new StringBuilder(String.valueOf(res))).append(child.toString()).toString();
    }
    res = (new StringBuilder(String.valueOf(res))).append("}").toString();
    return res;
  }

  /**
   * Returns apted.node data. Used especially for calculating rename cost.
   *
   * @return apted.node data (label of a apted.node).
   */
  public D getNodeData() {
    return nodeData;
  }

  /**
   * Sets the apted.node data of this apted.node.
   *
   * @param nodeData instance of apted.node data (apted.node label).
   */
  public void setNodeData(D nodeData) {
    this.nodeData = nodeData;
  }

  /**
   * Returns the vector with all apted.node's children.
   *
   * @return children of the apted.node.
   */
  public Vector<Node<D>> getChildren() {
    return children;
  }

}
