/*
 * Name: Michael Tenkorang
 * Class Purpose: Working with the HashMaps and Binary Search Trees
 */

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Stack;

@SuppressWarnings("unchecked")
public class AVLTreeMap<K, V> implements MapSet<K, V>, Iterable<MapSet.KeyValuePair<K, V>> {

    private static class Node<K, V> extends KeyValuePair<K, V> {
        Node<K, V> left, right;
        int height;

        public Node(K key, V value) {
            super(key, value);
            left = null;
            right = null;
            height = 1;
        }

        public Node(K key, V value, int size, int height) {
            super(key, value);
            left = null;
            right = null;
            this.height = height;
        }
    }

    private Node<K, V> root;
    private int size;
    private Comparator<K> comparator;

    public AVLTreeMap(Comparator<K> comparator) {
        root = null;
        size = 0;
        if (comparator != null) {
            this.comparator = comparator;
        } else {
            this.comparator = new Comparator<K>() {

                @Override
                public int compare(K o1, K o2) {
                    return ((Comparable<K>) o1).compareTo(o2);
                }

            };
        }
    }

    public AVLTreeMap() {
        this(null);
    }

    @Override
    public Iterator<KeyValuePair<K, V>> iterator() {
        return new InOrderIterator();
    }

    private class InOrderIterator implements Iterator<KeyValuePair<K, V>> {

        private Stack<Node<K, V>> stack;

        public InOrderIterator() {
            stack = new Stack<Node<K, V>>();
            pushLefts(root);
        }

        private void pushLefts(Node<K, V> node) {
            while (node != null) {
                stack.push(node);
                node = node.left;
            }
        }

        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        @Override
        public KeyValuePair<K, V> next() {
            Node<K, V> node = stack.pop();
            pushLefts(node.right);
            return node;
        }
    }

    // /*
    // * Return the size of the Tree
    // */
    // public int size() {
    // return size(root);
    // }

    // /*
    // * Returns the size attribute of a node or root of any subtree
    // */
    // private int size(Node<K, V> curNode) {
    // if (curNode == null)
    // return 0;
    // return curNode.size;
    // }

    /*
     * Returns the height of the tree
     */
    public int height() {
        return height(root);
    }

    /*
     * Returns the height attribute of a node or the height of a subtree with root
     * curNode
     */
    private int height(Node<K, V> curNode) {
        if (curNode == null) {
            return -1;
        }
        return curNode.height;
    }

    /*
     * Add a key-value pair to the tree
     */
    public V put(K key, V val) {
        V oldVal = null;
        if (containsKey(key)) {
            oldVal = this.get(key);
        }
        put(root, key, val);
        size++;
        return oldVal;
    }

    /*
     * Helper function for the put method
     */
    private Node<K, V> put(Node<K, V> curNode, K key, V val) {
        if (curNode == null) {
            return new Node<K, V>(key, val);
        }
        int cmp = comparator.compare(key, curNode.getKey());
        if (cmp < 0) {
            curNode.left = put(curNode.left, key, val);
        } else if (cmp > 0) {
            curNode.right = put(curNode.right, key, val);
        } else {
            curNode.setValue(val);
            return curNode;
        }
        // curNode.size = 1 + size(curNode.left) + size(curNode.right);
        curNode.height = 1 + Math.max(height(curNode.left), height(curNode.right));
        return balance(curNode);
    }

    /*
     * Checks for the balance factor of the node @param and performs rotations based
     * on their balance factors
     */
    private Node<K, V> balance(Node<K, V> curNode) {
        if (balanceFactor(curNode) < -1) {
            if (balanceFactor(curNode.right) > 0) {
                curNode.right = rotateRight(curNode.right);
            }
            curNode = rotateLeft(curNode);
        } else if (balanceFactor(curNode) > 1) {
            if (balanceFactor(curNode.left) < 0) {
                curNode.left = rotateLeft(curNode.left);
            }
            curNode = rotateRight(curNode);
        }
        return curNode;
    }

    /*
     * Returns the balance factor of the node @param
     */
    private int balanceFactor(Node<K, V> curNode) {
        return height(curNode.left) - height(curNode.right);
    }

    /*
     * Performs a right rotation on a subtree with root node @param
     */
    private Node<K, V> rotateRight(Node<K, V> curNode) {
        Node<K, V> curNodeLeft = curNode.left;
        curNode.left = curNodeLeft.right;
        curNodeLeft.right = curNode;
        // curNodeLeft.size = curNode.size;
        // curNode.size = 1 + size(curNode.left) + size(curNode.right);
        curNode.height = 1 + Math.max(height(curNode.left), height(curNode.right));
        curNodeLeft.height = 1 + Math.max(height(curNodeLeft.left), height(curNodeLeft.right));
        return curNodeLeft;
    }

    /*
     * Performs a left rotation on a subtree with root node @param
     */
    private Node<K, V> rotateLeft(Node<K, V> curNode) {
        Node<K, V> curNodeRight = curNode.right;
        curNode.right = curNodeRight.left;
        curNodeRight.left = curNode;
        // curNodeRight.size = curNode.size;
        // curNode.size = 1 + size(curNode.left) + size(curNode.right);
        curNode.height = 1 + Math.max(height(curNode.left), height(curNode.right));
        curNodeRight.height = 1 + Math.max(height(curNodeRight.left), height(curNodeRight.right));
        return curNodeRight;
    }

    @Override
    public boolean containsKey(K key) {
        if (root == null) {
            return false;
        }

        return containsKey(key, root);
    }

    private boolean containsKey(K key, Node<K, V> curNode) {
        if (comparator.compare(key, curNode.getKey()) > 0) {
            if (curNode.right != null) {
                return containsKey(key, curNode.right);
            } else {
                return false;
            }
        } else if (comparator.compare(key, curNode.getKey()) < 0) {
            if (curNode.left != null) {
                return containsKey(key, curNode.left);
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public V get(K key) {
        if (key == root.getKey()) {
            return root.getValue();
        } else {
            return get(key, root);
        }
    }

    private V get(K key, Node<K, V> curNode) {
        if (comparator.compare(key, curNode.getKey()) < 0) {
            if (curNode.left != null) {
                return get(key, curNode.left);
            } else {
                return null;
            }
        } else if (comparator.compare(key, curNode.getKey()) > 0) {
            if (curNode.right != null) {
                return get(key, curNode.right);
            } else {
                return null;
            }
        } else {
            return curNode.getValue();
        }
    }

    /*
     * Remove node with key value `key`
     * Returns the previous value
     */
    public V remove(K key) {
        if (!containsKey(key)) {
            return null;
        }
        V val = this.get(key);
        remove(root, key);

        return val;
    }

    /*
     * Helper function for the remove method that also keeps tree balanced
     */
    private Node<K, V> remove(Node<K, V> curNode, K key) {
        if (comparator.compare(key, curNode.getKey()) < 0) {
            curNode.left = remove(curNode.left, key);
        } else if (comparator.compare(key, curNode.getKey()) > 0) {
            curNode.right = remove(curNode.right, key);
        } else {
            if (curNode.left == null) {
                return curNode.right;
            } else if (curNode.right == null) {
                return curNode.left;
            } else {
                Node<K, V> tempNode = curNode;
                curNode = min(tempNode.right);
                curNode.right = deleteMin(tempNode.right);
                curNode.left = tempNode.left;
            }
        }
        // curNode.size = 1 + size(curNode.left) + size(curNode.right); // Update the
        // size
        curNode.height = 1 + Math.max(height(curNode.left), height(curNode.right)); // Update the height
        return balance(curNode); // ensure that the node is balanced
    }

    /*
     * Get the leftmost child of a tree
     */
    public K min() {
        return min(root).getKey();
    }

    /*
     * Return the leftmost child, given the root node of the subtree
     */
    private Node<K, V> min(Node<K, V> curNode) {
        if (curNode.left == null)
            return curNode;
        Node<K, V> min = min(curNode.left);
        return min;
    }

    /*
     * Delete the node with the smallest value in the tree
     */
    public void deleteMin() {
        root = deleteMin(root);
    }

    /*
     * Delete the node with the smallest value in the subtree with root curNode
     * while
     * keeping the tree balanced and keeping node attributes up-to-date
     */
    private Node<K, V> deleteMin(Node<K, V> curNode) {
        if (curNode.left == null) {
            return curNode.right;
        }
        curNode.left = deleteMin(curNode.left);
        // curNode.size = 1 + size(curNode.left) + size(curNode.right); // update the
        // size of every node along the path of the deleted node
        curNode.height = 1 + Math.max(height(curNode.left), height(curNode.right)); // update the height of every node
                                                                                    // along the path of the deleted
                                                                                    // node
        return balance(curNode); // balance each ancestor
    }

    public String toString() {
        return toString(root, "root: ", "");
    }

    private String toString(Node<K, V> curNode, String mainString, String childrenString) {
        if (curNode == null) {
            return "";
        }
        String result = childrenString + mainString + "<" + curNode.getKey() + " -> " + curNode.getValue() + ">\n";
        if (curNode.left != null) {
            result += toString(curNode.left, "left: ", childrenString + "    ");
        }

        if (curNode.right != null) {
            result += toString(curNode.right, "right: ", childrenString + "    ");
        }

        return result;
    }

    @Override
    public ArrayList<K> keySet() {
        if (size() == 0) {
            return new ArrayList<>();
        }

        ArrayList<K> output = new ArrayList<>();
        return keySet(root, output);
    }

    private ArrayList<K> keySet(Node<K, V> curNode, ArrayList<K> output) {
        if (curNode == null) {
            return null;
        }

        keySet(curNode.left, output);
        output.add(curNode.getKey());
        keySet(curNode.right, output);

        return output;
    }

    @Override
    public ArrayList<V> values() {
        if (size() == 0) {
            return new ArrayList<>();
        }

        ArrayList<V> output = new ArrayList<>();
        return values(root, output);
    }

    private ArrayList<V> values(Node<K, V> curNode, ArrayList<V> output) {
        if (curNode == null) {
            return null;
        }

        values(curNode.left, output);
        output.add(curNode.getValue());
        values(curNode.right, output);

        return output;
    }

    @Override
    public ArrayList<KeyValuePair<K, V>> entrySet() {
        if (size() == 0) {
            return new ArrayList<>();
        }

        ArrayList<KeyValuePair<K, V>> output = new ArrayList<>();
        return entrySet(root, output);
    }

    private ArrayList<KeyValuePair<K, V>> entrySet(Node<K, V> curNode, ArrayList<KeyValuePair<K, V>> output) {
        if (curNode == null) {
            return null;
        }

        entrySet(curNode.left, output);
        output.add(new KeyValuePair<K, V>(curNode.getKey(), curNode.getValue()));
        entrySet(curNode.right, output);

        return output;
    }

    @Override
    public void clear() {
        this.root = null;
    }

    @Override
    public int maxDepth() {
        return maxDepth(root);
    }

    private int maxDepth(Node<K, V> curNode) {
        if (curNode == null) {
            return 0;
        }

        int leftDepth = maxDepth(curNode.left);
        int rightDepth = maxDepth(curNode.right);
        return 1 + Math.max(leftDepth, rightDepth);
    }

    public static void main(String[] args) {
        AVLTreeMap<Integer, String> myTree = new AVLTreeMap<>();

        myTree.put(10, "Michael");
        myTree.put(200, "Joanne");
        myTree.put(112, "Abigail");
        myTree.put(150, "Abigail");
        myTree.put(0, "Abigail");
        myTree.put(15, "Mummy");

        System.out.println(myTree);

        for (MapSet.KeyValuePair<Integer, String> curNode : myTree) {
            System.out.println(curNode.getValue());
        }
    }

    @Override
    public int size() {
        return size;
    }
}