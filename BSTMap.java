import java.util.ArrayList;
import java.util.Comparator;

@SuppressWarnings("unchecked")
public class BSTMap<K, V> implements MapSet<K, V> {

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
            value = curNode.getValue();
            curNode.setValue(value);
            return value;
        }
    }

    @Override
    public boolean containsKey(K key) {
        if (key == null) {
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

        myTree.put(10, "Michael");
        myTree.put(20, "Michael");
        myTree.put(1, "Michael");
        myTree.put(15, "Michael");

        System.out.println(myTree);
    }
}