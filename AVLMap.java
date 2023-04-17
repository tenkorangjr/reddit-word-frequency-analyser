import java.util.ArrayList;
import java.util.Comparator;

@SuppressWarnings("unchecked")
public class AVLMap<K, V> implements MapSet<K, V> {

    private static class Node<K, V> extends KeyValuePair<K, V> {
        Node<K, V> left, right;
        int balanceFactor, height;

        public Node(K key, V value) {
            super(key, value);
            left = null;
            right = null;
            height = 1;
            balanceFactor = 0;
        }
    }

    private int size;
    private Node<K, V> root;
    private Comparator<K> comparator;

    public AVLMap(Comparator<K> comparator) {
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

    public AVLMap() {
        this(null);
    }

    @Override
    public V put(K key, V value) {
        if (size == 0) {
            root = new Node<>(key, value);
            size++;
            return null;
        } else {
            V oldVal = put(key, value, root);
            balanceTree(root);
            return oldVal;
        }
    }

    public V put(K key, V value, Node<K, V> curNode) {

        V oldVal = null;

        int cmp = comparator.compare(key, curNode.getKey());
        if (cmp < 0) {
            if (curNode.left == null) {
                curNode.left = new Node<>(key, value);
                size++;
            } else {
                oldVal = put(key, value, curNode.left);
            }
        } else if (cmp > 0) {
            if (curNode.right == null) {
                curNode.right = new Node<>(key, value);
                size++;
            } else {
                oldVal = put(key, value, curNode.right);
            }
        } else {
            oldVal = curNode.getValue();
            curNode.setValue(value);
            return oldVal;
        }

        curNode.balanceFactor = height(curNode.left) - height(curNode.right);

        if (curNode.balanceFactor > 1) {
            if (comparator.compare(key, curNode.left.getKey()) < 0) {
                curNode = rotateRight(curNode);
            } else {
                curNode.left = rotateLeft(curNode.left);
                curNode = rotateRight(curNode);
            }
        } else if (curNode.balanceFactor < -1) {
            if (comparator.compare(key, curNode.right.getKey()) > 0) {
                curNode = rotateLeft(curNode);
            } else {
                curNode.right = rotateRight(curNode.right);
                curNode = rotateLeft(curNode);
            }
        }

        return oldVal;
    }

    private int height(Node<K, V> node) {
        if (node == null) {
            return -1;
        }
        return Math.max(height(node.left), height(node.right)) + 1;
    }

    private Node<K, V> rotateRight(Node<K, V> node) {
        Node<K, V> leftChild = node.left;

        node.left = leftChild.right;
        leftChild.right = node;

        node.height = Math.max(height(node.left), height(node.right)) + 1;
        leftChild.height = Math.max(height(leftChild.left), height(leftChild.right)) + 1;

        node.balanceFactor = height(node.left) - height(node.right);
        leftChild.balanceFactor = height(leftChild.left) - height(leftChild.right);

        return leftChild;
    }

    private Node<K, V> rotateLeft(Node<K, V> node) {
        Node<K, V> rightChild = node.right;

        node.right = rightChild.left;
        rightChild.left = node;

        node.height = Math.max(height(node.left), height(node.right)) + 1;
        rightChild.height = Math.max(height(rightChild.left), height(rightChild.right)) + 1;

        node.balanceFactor = height(node.left) - height(node.right);
        rightChild.balanceFactor = height(rightChild.left) - height(rightChild.right);

        return rightChild;
    }

    private void balanceTree(Node<K, V> node) {
        if (node.balanceFactor > 1) {
            if (node.left.balanceFactor >= 0) {
                node = rotateRight(node);
            } else {
                node.left = rotateLeft(node.left);
                node = rotateRight(node);
            }
        } else if (node.balanceFactor < -1) {
            if (node.right.balanceFactor <= 0) {
                node = rotateLeft(node);
            } else {
                node.right = rotateRight(node.right);
                node = rotateLeft(node);
            }
        }

        if (node.left != null) {
            balanceTree(node.left);
        }

        if (node.right != null) {
            balanceTree(node.right);
        }
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

    public String toString() {
        return toString(root, "root: ", "");
    }

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
    public int size() {
        return this.size;
    }

    @Override
    public void clear() {
        this.size = 0;
        this.root = null;
    }

    @Override
    public int maxDepth() {
        return maxDepth(root);
    }

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

        myTree.put(20, "Michael");
        myTree.put(1, "Michael");
        myTree.put(10, "Michael");
        myTree.put(30, "Michael");
        myTree.put(40, "Michael");
        myTree.put(50, "Michael");
        myTree.put(60, "Michael");
        myTree.put(70, "Michael");
        myTree.put(80, "Michael");
        myTree.put(90, "Michael");

        System.out.println(myTree);
    }
}