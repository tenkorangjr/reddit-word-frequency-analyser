import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Stack;


@SuppressWarnings("unchecked")
public class AVLTreeMap<K, V> implements MapSet<K, V>, Iterable<MapSet.KeyValuePair<K, V>> {

    private static class Node<K, V> extends KeyValuePair<K, V> {
        Node<K, V> left, right;
        int size, height;

        public Node(K key, V value) {
            super(key, value);
            left = null;
            right = null;
            size = 0;
            height = 1;
        }

        public Node(K key, V value, int size , int height) {
            super(key, value);
            left = null;
            right = null;
            this.size = size;
            this.height = height;
        }
    }

    private Node<K, V> root;
    private Comparator<K> comparator;

    public AVLTreeMap(Comparator<K> comparator) {
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

    /**
     * Returns the number key-value pairs in the symbol table.
     * 
     * @return the number key-value pairs in the symbol table
     */
    public int size() {
        return size(root);
    }

    /**
     * Returns the number of nodes in the subtree.
     * 
     * @param x the subtree
     * 
     * @return the number of nodes in the subtree
     */
    private int size(Node<K, V> curNode) {
        if (curNode == null) return 0;
        return curNode.size;
    }

    public int height(){
        return height(root);
    }

    private int height(Node<K, V> curNode){
        if (curNode == null){
            return -1;
        } return curNode.height;
    }

    @Override
    /**
     * Inserts the specified key-value pair into the symbol table, overwriting
     * the old value with the new value if the symbol table already contains the
     * specified key. Deletes the specified key (and its associated value) from
     * this symbol table if the specified value is {@code null}.
     * 
     * @param key the key
     * @param val the value
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public V put(K key, V val) {
        V oldVal = null;
        if (val == null) {
            return null;
        }
        if (containsKey(key)){
            oldVal = this.get(key);
        }
        root = put(root, key, val);
        return oldVal;
    }

    /**
     * Inserts the key-value pair in the subtree. It overrides the old value
     * with the new value if the symbol table already contains the specified key
     * and deletes the specified key (and its associated value) from this symbol
     * table if the specified value is {@code null}.
     * 
     * @param x the subtree
     * @param key the key
     * @param val the value
     * @return the subtree
     */
    private Node<K, V> put(Node<K, V> curNode, K key, V val) {
        if (curNode == null){
            return new Node<K, V>(key, val, 0, 1);
        }
        int cmp = ((Comparable<K>) key).compareTo(curNode.getKey());
        if (cmp < 0) {
            curNode.left = put(curNode.left, key, val);
        }
        else if (cmp > 0) {
            curNode.right = put(curNode.right, key, val);
        }
        else {
            curNode.setValue(val);
            return curNode;
        }
        curNode.size = 1 + size(curNode.left) + size(curNode.right);
        curNode.height = 1 + Math.max(height(curNode.left), height(curNode.right));
        return balance(curNode);
    }

    private Node<K, V> balance(Node<K, V> curNode) {
        if (balanceFactor(curNode) < -1) {
            if (balanceFactor(curNode.right) > 0) {
                curNode.right = rotateRight(curNode.right);
            }
            curNode = rotateLeft(curNode);
        }
        else if (balanceFactor(curNode) > 1) {
            if (balanceFactor(curNode.left) < 0) {
                curNode.left = rotateLeft(curNode.left);
            }
            curNode = rotateRight(curNode);
        }
        return curNode;
    }

    private int balanceFactor(Node<K, V> x) {
        return height(x.left) - height(x.right);
    }

    private Node<K, V> rotateRight(Node<K, V> x) {
        Node<K, V> y = x.left;
        x.left = y.right;
        y.right = x;
        y.size = x.size;
        x.size = 1 + size(x.left) + size(x.right);
        x.height = 1 + Math.max(height(x.left), height(x.right));
        y.height = 1 + Math.max(height(y.left), height(y.right));
        return y;
    }

    /**
     * Rotates the given subtree to the left.
     * 
     * @param x the subtree
     * @return the left rotated subtree
     */
    private Node<K, V> rotateLeft(Node<K, V> curNode) {
        Node<K, V> tempNode = curNode.right;
        curNode.right = tempNode.left;
        tempNode.left = curNode;
        tempNode.size = curNode.size;
        curNode.size = 1 + size(curNode.left) + size(curNode.right);
        curNode.height = 1 + Math.max(height(curNode.left), height(curNode.right));
        tempNode.height = 1 + Math.max(height(tempNode.left), height(tempNode.right));
        return tempNode;
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

    /**
     * Removes the specified key and its associated value from the symbol table
     * (if the key is in the symbol table).
     * 
     * @param key the key
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public V remove(K key) {
        if (!containsKey(key)){
            return null;
        }
        V oldVal = this.get(key);
        root = remove(root, key);
        
        return oldVal;
    }

    /**
     * Removes the specified key and its associated value from the given
     * subtree.
     * 
     * @param x the subtree
     * @param key the key
     * @return the updated subtree
     */
    private Node<K, V> remove(Node<K, V> curNode, K key) {
        int cmp = comparator.compare(key, curNode.getKey());
        if (cmp < 0) {
            curNode.left = remove(curNode.left, key);
        }
        else if (cmp > 0) {
            curNode.right = remove(curNode.right, key);
        }
        else {
            if (curNode.left == null) {
                return curNode.right;
            }
            else if (curNode.right == null) {
                return curNode.left;
            }
            else {
                Node<K, V> tempNode = curNode;
                curNode = min(tempNode.right);
                curNode.right = deleteMin(tempNode.right);
                curNode.left = tempNode.left;
            }
        }
        curNode.size = 1 + size(curNode.left) + size(curNode.right);
        curNode.height = 1 + Math.max(height(curNode.left), height(curNode.right));
        return balance(curNode);
    }

    public K min() {
        return min(root).getKey();
    }

    /**
     * Returns the node with the smallest key in the subtree.
     * 
     * @param x the subtree
     * @return the node with the smallest key in the subtree
     */
    private Node<K, V> min(Node<K, V> x) {
        if (x.left == null) return x;
        return min(x.left);
    }

    /**
     * Removes the smallest key and associated value from the symbol table.
     * 
     * @throws NoSuchElementException if the symbol table is empty
     */
    public void deleteMin() {
        root = deleteMin(root);
    }

    /**
     * Removes the smallest key and associated value from the given subtree.
     * 
     * @param x the subtree
     * @return the updated subtree
     */
    private Node<K,V> deleteMin(Node<K,V> curNode) {
        if (curNode.left == null) return curNode.right;
        curNode.left = deleteMin(curNode.left);
        curNode.size = 1 + size(curNode.left) + size(curNode.right);
        curNode.height = 1 + Math.max(height(curNode.left), height(curNode.right));
        return balance(curNode);
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
    public void clear() {
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
        AVLTreeMap<Integer, String> myTree = new AVLTreeMap<>();

        myTree.put(10, "Michael");
        myTree.put(20, "Joanne");
        myTree.put(112, "Abigail");
        myTree.put(150, "Abigail");
        myTree.put(0, "Abigail");
        myTree.put(15, "Mummy");

        System.out.println(myTree);

        for (MapSet.KeyValuePair<Integer, String> curNode: myTree){
            System.out.println(curNode.getValue());
        }
    }
}