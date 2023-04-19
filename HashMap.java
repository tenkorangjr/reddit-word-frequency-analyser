import java.util.ArrayList;
import java.util.Iterator;

@SuppressWarnings("unchecked")
public class HashMap<K, V> implements MapSet<K, V>, Iterable<HashMap.Node<K, V>> {

    public static class Node<K, V> extends MapSet.KeyValuePair<K, V> {
        Node<K, V> next;

        public Node(K key, V value, Node<K, V> next) {
            super(key, value);
            this.next = next;
        }
    }

    private Node<K, V>[] buckets;
    private int size;
    private double maxLoadFactor;

    public HashMap(int initialCapacity, double maxLoadFactor) {
        size = 0;
        buckets = (Node<K, V>[]) new Node[initialCapacity];
        this.maxLoadFactor = maxLoadFactor;
    }

    public HashMap(int initialCapacity) {
        this(initialCapacity, .75);
    }

    public HashMap() {
        this(16);
    }

    public Iterator<Node<K, V>> iterator() {
        return new HashMapIterator();
    }

    private class HashMapIterator implements Iterator<Node<K, V>> {
        private int bucketIndex;
        private Node<K, V> currentNode;

        public HashMapIterator() {
            bucketIndex = -1;
            currentNode = null;
            for (int i = 0; i < buckets.length; i++) {
                if (buckets[i] != null) {
                    bucketIndex = i;
                    currentNode = buckets[i];
                    break;
                }
            }
        }

        @Override
        public boolean hasNext() {
            return currentNode != null;
        }

        @Override
        public Node<K, V> next() {
            Node<K, V> result = currentNode;
            if (currentNode.next != null) {
                currentNode = currentNode.next;
            } else {
                bucketIndex++;
                while (bucketIndex < buckets.length && buckets[bucketIndex] == null) {
                    bucketIndex++;
                }
                if (bucketIndex == buckets.length) {
                    currentNode = null;
                } else {
                    currentNode = buckets[bucketIndex];
                }
            }
            return result;
        }
    }

    private int capacity() {
        return buckets.length;
    }

    private int hash(K key) {
        return Math.abs(key.hashCode() % capacity());
    }

    @Override
    public V put(K key, V value) {
        int index = hash(key);

        if (buckets[index] == null) {
            buckets[index] = new Node<K, V>(key, value, null);
        } else {
            for (Node<K, V> curNode = buckets[index]; curNode != null; curNode = curNode.next) {
                if (curNode.getKey().equals(key)) {
                    V oldVal = curNode.getValue();
                    curNode.setValue(value);
                    return oldVal;
                }
            }

            buckets[index] = new Node<K, V>(key, value, buckets[index]);
        }

        size++;
        if (size > maxLoadFactor * capacity()) {
            resize(capacity() * 2);
        }

        return null;
    }

    public V put(Node<K, V>[] buckets, K key, V value) {
        int index = hash(key);

        if (buckets[index] == null) {
            buckets[index] = new Node<K, V>(key, value, null);
        } else {
            for (Node<K, V> curNode = buckets[index]; curNode != null; curNode = curNode.next) {
                if (curNode.getKey().equals(key)) {
                    V oldVal = curNode.getValue();
                    curNode.setValue(oldVal);
                    return oldVal;
                }
            }

            buckets[index] = new Node<K, V>(key, value, buckets[index]);
        }

        size++;
        if (size > maxLoadFactor * capacity()) {
            resize(capacity() * 2);
        }

        return null;
    }

    private void resize(int newSize) {
        Node<K, V>[] oldBuckets = this.buckets;
        this.buckets = (Node<K, V>[]) new Node[newSize];

        size = 0;
        for (Node<K, V> curNode : oldBuckets) {
            if (curNode != null) {
                for (Node<K, V> iNode = curNode; iNode != null; iNode = iNode.next) {
                    put(iNode.getKey(), iNode.getValue());
                }
            }
        }

    }

    @Override
    public boolean containsKey(K key) {
        int index = hash(key);

        if (buckets[index] == null) {
            return false;
        } else {
            for (Node<K, V> curNode = buckets[index]; curNode != null; curNode = curNode.next) {
                if (curNode.getKey().equals(key)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public V get(K key) {
        int index = hash(key);

        if (buckets[index] == null) {
            return null;
        } else {
            for (Node<K, V> curNode = buckets[index]; curNode != null; curNode = curNode.next) {
                if (curNode.getKey().equals(key)) {
                    return curNode.getValue();
                }
            }
        }

        return null;
    }

    @Override
    public V remove(K key) {
        int index = hash(key);

        if (buckets[index] == null) {
            return null;
        } else if (buckets[index].getKey().equals(key)) {
            Node<K, V> curr = buckets[index];
            V output = curr.getValue();
            buckets[index] = curr.next;
            size--;

            if (size < (capacity() * maxLoadFactor) / 4) {
                resize(capacity() / 2);
            }
            return output;
        } else {
            Node<K, V> prev = null;
            for (Node<K, V> curNode = buckets[index]; curNode != null; curNode = curNode.next) {
                if (curNode.next.getKey().equals(key)) {
                    prev = curNode;
                    break;
                }
            }

            if (prev.next.getKey().equals(key)) {
                V output = prev.next.getValue();
                prev.next = prev.next.next;
                size--;

                if (size < (capacity() * maxLoadFactor) / 4) {
                    resize(capacity() / 2);
                }
                return output;
            }

            return null;
        }
    }

    @Override
    public ArrayList<K> keySet() {
        ArrayList<K> output = new ArrayList<>();
        for (Node<K, V> curNode : this.buckets) {
            Node<K, V> walker = curNode;
            while (walker != null) {
                output.add(walker.getKey());
                walker = walker.next;
            }
        }

        return output;
    }

    @Override
    public ArrayList<V> values() {
        ArrayList<V> output = new ArrayList<>();
        for (Node<K, V> curNode : this.buckets) {
            Node<K, V> walker = curNode;
            while (walker != null) {
                output.add(walker.getValue());
                walker = walker.next;
            }
        }

        return output;
    }

    @Override
    public ArrayList<KeyValuePair<K, V>> entrySet() {
        ArrayList<KeyValuePair<K, V>> output = new ArrayList<>();
        for (Node<K, V> curNode : this.buckets) {
            Node<K, V> walker = curNode;
            while (walker != null) {
                output.add(new KeyValuePair<K, V>(walker.getKey(), walker.getValue()));
                walker = walker.next;
            }
        }

        return output;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void clear() {
        this.buckets = new Node[16];
        this.size = 0;
    }

    @Override
    public int maxDepth() {
        int maxDepth = 0;
        int curDepth = 0;

        for (Node<K, V> curNode : this.buckets) {
            Node<K, V> walker = curNode;
            while (walker != null) {
                walker = walker.next;
                curDepth++;
            }

            if (curDepth > maxDepth) {
                maxDepth = curDepth;
            }

            curDepth = 0;
        }

        return maxDepth;
    }

    public String toString() {
        String output = "\n";

        for (Node<K, V> curNode : this.buckets) {
            Node<K, V> walker = curNode;
            while (walker != null) {
                output += curNode.toString() + "\n";
                walker = walker.next;
            }
        }

        return output;

    }

    public static void main(String[] args) {
        // HashMap<String, Integer> myDict = new HashMap<>();

        // myDict.put("Michael", 10);
        // myDict.put("Tracy", 20);
        // myDict.put("Vanessa", 50);
        // myDict.put("Joanne", 90);
        // myDict.put("Abi", 100);

        // for(Node<String, Integer> curNode: myDict){
        //     System.out.println(curNode.getKey());
        // }

        // System.out.println(myDict.remove(21));

        // System.out.println(myDict);

        // System.out.println(myDict.keySet());
        // System.out.println(myDict.values());
        // System.out.println(myDict.entrySet());
    }

}
