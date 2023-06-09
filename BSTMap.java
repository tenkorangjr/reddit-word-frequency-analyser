/*
 * Name: Michael Tenkorang
 * Class Purpose: Working with the HashMaps and Binary Search Trees
 */

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Stack;

@SuppressWarnings("unchecked")
public class BSTMap<K, V> implements MapSet<K, V>, Iterable<MapSet.KeyValuePair<K, V>> {

    private static class Node<K, V> extends KeyValuePair<K, V> {
        Node<K, V> left, right;

        public Node(K key, V value) {
            super(key, value);
            left = null;
            right = null;
        }
    }

    private int size;
    private Node<K, V> root;
    private Comparator<K> comparator;

    public BSTMap(Comparator<K> comparator) {
        size = 0;
        root = null;
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

    public BSTMap() {
        this(null);
    }

    /*
     * Iterator for the BST Map
     */
    @Override
    public Iterator<KeyValuePair<K, V>> iterator() {
        return new InOrderIterator();
    }

    private class InOrderIterator implements Iterator<KeyValuePair<K, V>> {

        private Stack<Node<K, V>> stack;

        public InOrderIterator() {
            stack = new Stack<Node<K, V>>();
            pushLeft(root);
        }

        /*
         * Pushes all the lefts unto the stack
         */
        private void pushLeft(Node<K, V> node) {
            while (node != null) {
                stack.push(node);
                node = node.left;
            }
        }

        /*
         * Check if the curNode has nexts
         */
        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        /*
         * Get the next node in the tree
         */
        @Override
        public KeyValuePair<K, V> next() {
            Node<K, V> node = stack.pop();
            pushLeft(node.right);
            return node;
        }
    }

    /*
     * Add item to the tree
     */
    @Override
    public V put(K key, V value) {
        if (size == 0) {
            root = new Node<>(key, value);
            size++;
            return null;
        } else {
            return put(key, value, root);
        }
    }

    /*
     * Helper function for put method
     */
    public V put(K key, V value, Node<K, V> curNode) {
        if (comparator.compare(key, curNode.getKey()) < 0) {
            if (curNode.left == null) {
                curNode.left = new Node<>(key, value);
                size++;
                return null;
            } else {
                return put(key, value, curNode.left);
            }
        } else if (comparator.compare(key, curNode.getKey()) > 0) {
            if (curNode.right == null) {
                curNode.right = new Node<>(key, value);
                size++;
                return null;
            } else {
                return put(key, value, curNode.right);
            }
        } else {
            V oldVal = curNode.getValue();
            curNode.setValue(value);
            return oldVal;
        }
    }

    /*
     * Check if a tree contains key
     */
    @Override
    public boolean containsKey(K key) {
        if (root == null) {
            return false;
        }

        return containsKey(key, root);
    }

    /*
     * Helper method for the contains method
     */
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

    /*
     * Get the corresponding value of a key
     */
    @Override
    public V get(K key) {
        if (key == root.getKey()) {
            return root.getValue();
        } else {
            return get(key, root);
        }
    }

    /*
     * Helper method for the get method
     */
    private V get(K key, Node<K, V> cur) {
        if (comparator.compare(key, cur.getKey()) < 0) {
            if (cur.left != null) {
                return get(key, cur.left);
            } else {
                return null;
            }
        } else if (comparator.compare(key, cur.getKey()) > 0) {
            if (cur.right != null) {
                return get(key, cur.right);
            } else {
                return null;
            }
        } else {
            return cur.getValue();
        }
    }

    /*
     * Remove a key from the tree
     */
    @Override
    public V remove(K key) {
        Node<K, V> toDeleteParent = null;
        Node<K, V> toDelete = root;

        if (!containsKey(key)) {
            return null;
        }

        while (toDelete != null) {
            if (comparator.compare(key, toDelete.getKey()) < 0) {
                toDeleteParent = toDelete;
                toDelete = toDelete.left;
            } else if (comparator.compare(key, toDelete.getKey()) > 0) {
                toDeleteParent = toDelete;
                toDelete = toDelete.right;
            } else {
                break;
            }
        }

        if (toDelete == null) {
            return null;
        }

        V value = toDelete.getValue();
        handleReplacement(toDelete, toDeleteParent);
        size--;

        return value;
    }

    /*
     * Handle the replacement of a deleted node
     */
    private void handleReplacement(Node<K, V> toDelete, Node<K, V> toDeleteParent) {
        if (toDelete.left == null && toDelete.right == null) {
            if (toDeleteParent == null) {
                root = null;
            } else if (toDeleteParent.left == toDelete) {
                toDeleteParent.left = null;
            } else {
                toDeleteParent.right = null;
            }
        } else if (toDelete.left == null) {
            if (toDeleteParent == null) {
                root = toDelete.right;
            } else if (toDeleteParent.left == toDelete) {
                toDeleteParent.left = toDelete.right;
            } else {
                toDeleteParent.right = toDelete.right;
            }
        } else if (toDelete.right == null) {
            if (toDeleteParent == null) {
                root = toDelete.left;
            } else if (toDeleteParent.left == toDelete) {
                toDeleteParent.left = toDelete.left;
            } else {
                toDeleteParent.right = toDelete.left;
            }
        } else {
            Node<K, V> successorParent = toDelete;
            Node<K, V> successor = toDelete.right;

            while (successor.left != null) {
                successorParent = successor;
                successor = successor.left;
            }

            if (successorParent != toDelete) {
                successorParent.left = successor.right;
                successor.right = toDelete.right;
            }

            if (toDeleteParent == null) {
                root = successor;
            } else if (toDeleteParent.left == toDelete) {
                toDeleteParent.left = successor;
            } else {
                toDeleteParent.right = successor;
            }

            successor.left = toDelete.left;
        }
    }

    /*
     * String representation of the tree
     */
    public String toString() {
        return toString(root, "root: ", "");
    }

    /*
     * Helper method for the toString method
     */
    private String toString(Node<K, V> currNode, String mainString, String childrenString) {
        if (currNode == null) {
            return "";
        }
        String result = childrenString + mainString + "<" + currNode.getKey() + " -> " + currNode.getValue() + ">\n";
        if (currNode.left != null) {
            result += toString(currNode.left, "left: ", childrenString + "    ");
        }

        if (currNode.right != null) {
            result += toString(currNode.right, "right: ", childrenString + "    ");
        }

        return result;
    }

    /*
     * Returns a list of the keys in the tree
     */
    @Override
    public ArrayList<K> keySet() {
        if (size() == 0) {
            return new ArrayList<>();
        }

        ArrayList<K> output = new ArrayList<>();
        return keySet(root, output);
    }

    /*
     * Helper function for the keySet() method
     */
    private ArrayList<K> keySet(Node<K, V> curNode, ArrayList<K> output) {
        if (curNode == null) {
            return null;
        }

        keySet(curNode.left, output);
        output.add(curNode.getKey());
        keySet(curNode.right, output);

        return output;
    }

    /*
     * Return the list of all the values of the tree
     */
    @Override
    public ArrayList<V> values() {
        if (size() == 0) {
            return new ArrayList<>();
        }

        ArrayList<V> output = new ArrayList<>();
        return values(root, output);
    }

    /*
     * Helper method for the values() method
     */
    private ArrayList<V> values(Node<K, V> curNode, ArrayList<V> output) {
        if (curNode == null) {
            return null;
        }

        values(curNode.left, output);
        output.add(curNode.getValue());
        values(curNode.right, output);

        return output;
    }

    /*
     * Returns the list of all the key-value pairs in the tree
     */
    @Override
    public ArrayList<KeyValuePair<K, V>> entrySet() {
        if (size() == 0) {
            return new ArrayList<>();
        }

        ArrayList<KeyValuePair<K, V>> output = new ArrayList<>();
        return entrySet(root, output);
    }

    /*
     * Helper method for the entrySet() method
     */
    private ArrayList<KeyValuePair<K, V>> entrySet(Node<K, V> curNode, ArrayList<KeyValuePair<K, V>> output) {
        if (curNode == null) {
            return null;
        }

        entrySet(curNode.left, output);
        output.add(new KeyValuePair<K, V>(curNode.getKey(), curNode.getValue()));
        entrySet(curNode.right, output);

        return output;
    }

    /*
     * Return the size of the tree
     */
    @Override
    public int size() {
        return this.size;
    }

    /*
     * Reset the map
     */
    @Override
    public void clear() {
        this.size = 0;
        this.root = null;
    }

    /*
     * Get the maxDepth of the tree
     */
    @Override
    public int maxDepth() {
        return maxDepth(root);
    }

    /*
     * Helper function for the maxDepth
     */
    private int maxDepth(Node<K, V> node) {
        if (node == null) {
            return 0;
        }

        int leftDepth = maxDepth(node.left);
        int rightDepth = maxDepth(node.right);
        return 1 + Math.max(leftDepth, rightDepth);
    }

    public static void main(String[] args) {
        BSTMap<Integer, String> myTree = new BSTMap<>();

        myTree.put(10, "Michael");
        myTree.put(20, "Joanne");
        myTree.put(1, "Abigail");
        myTree.put(15, "Mummy");

        System.out.println(myTree);

        for (MapSet.KeyValuePair<Integer, String> curNode : myTree) {
            System.out.println(curNode.getValue());
        }
    }
}