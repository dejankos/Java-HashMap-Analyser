package io.github.dejankos.hashmap.analyser.model;

public class NodeData<K, V> {
    private final K key;
    private final V value;
    private final int hashCode;

    public NodeData(K key, V value, int hashCode) {
        this.key = key;
        this.value = value;
        this.hashCode = hashCode;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public int getHashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return "NodeData{" +
                "key=" + key +
                ", value=" + value +
                ", hashCode=" + hashCode +
                '}';
    }
}
