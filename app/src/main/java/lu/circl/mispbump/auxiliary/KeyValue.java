package lu.circl.mispbump.auxiliary;

public class KeyValue<K, V> {
    public K key;
    public V value;

    /**
     * Generates a generic key value pair.
     * @param key key
     * @param value value
     */
    public KeyValue(K key, V value) {
        this.key = key;
        this.value = value;
    }
}
