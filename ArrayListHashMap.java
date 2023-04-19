import java.util.ArrayList;

public class ArrayListHashMap<K, V> implements MapSet<K, V>{

    private ArrayList<ArrayList<KeyValuePair<K, V>>> buckets;
    private int size;

    public ArrayListHashMap() {
        buckets = new ArrayList<ArrayList<KeyValuePair<K, V>>>();
        for (int i = 0; i < 16; i++) {
            buckets.add(new ArrayList<KeyValuePair<K, V>>());
        }
        size = 0;
    }

    @Override
    public V put(K key, V value) {
        int bucketIndex = key.hashCode() % buckets.size();
        ArrayList<KeyValuePair<K, V>> bucket = buckets.get(bucketIndex);
        for (KeyValuePair<K, V> kvp : bucket) {
            if (kvp.getKey().equals(key)) {
                V oldValue = kvp.getValue();
                kvp.setValue(value);;
                return oldValue;
            }
        }
        bucket.add(new KeyValuePair<K, V>(key, value));
        size++;
        return null;
    }

    @Override
    public boolean containsKey(K key) {
        int bucketIndex = key.hashCode() % buckets.size();
        ArrayList<KeyValuePair<K, V>> bucket = buckets.get(bucketIndex);
        for (KeyValuePair<K, V> kvp : bucket) {
            if (kvp.getKey().equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public V get(K key) {
        int bucketIndex = key.hashCode() % buckets.size();
        ArrayList<KeyValuePair<K, V>> bucket = buckets.get(bucketIndex);
        for (KeyValuePair<K, V> kvp : bucket) {
            if (kvp.getKey().equals(key)) {
                return kvp.getValue();
            }
        }
        return null;
    }

    @Override
    public V remove(K key) {
        int bucketIndex = key.hashCode() % buckets.size();
        ArrayList<KeyValuePair<K, V>> bucket = buckets.get(bucketIndex);
        for (int i = 0; i < bucket.size(); i++) {
            KeyValuePair<K, V> kvp = bucket.get(i);
            if (kvp.getKey().equals(key)) {
                bucket.remove(i);
                size--;
                return kvp.getValue();
            }
        }
        return null;
    }

    @Override
    public ArrayList<K> keySet() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'keySet'");
    }

    @Override
    public ArrayList<V> values() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'values'");
    }

    @Override
    public ArrayList<MapSet.KeyValuePair<K, V>> entrySet() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'entrySet'");
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        for (ArrayList<KeyValuePair<K, V>> bucket : buckets) {
            bucket.clear();
        }
        size = 0;
    }

    @Override
    public int maxDepth() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'maxDepth'");
    }

}
