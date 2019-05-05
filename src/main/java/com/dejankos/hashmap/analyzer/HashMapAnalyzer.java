package com.dejankos.hashmap.analyzer;

import com.dejankos.hashmap.analyzer.model.BucketMetadata;
import com.dejankos.hashmap.analyzer.model.HashMapMetadata;
import com.dejankos.hashmap.analyzer.model.NodeData;
import com.dejankos.hashmap.analyzer.model.NodeType;
import com.dejankos.hashmap.analyzer.util.FieldUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class HashMapAnalyzer<K, V> {

    @SuppressWarnings("unchecked")
    private static final HashMapMetadata EMPTY = new HashMapMetadata(0, 0, emptyList());

    private final Class<K> keyClass;
    private final Class<V> valueClass;

    public HashMapAnalyzer(Class<K> keyClass, Class<V> valueClass) {
        this.keyClass = keyClass;
        this.valueClass = valueClass;
    }

    public HashMapMetadata<K, V> analyse(HashMap<K, V> map) {
        if (isNull(map)) {
            return null;
        }

        if (map.isEmpty()) {
            //noinspection unchecked
            return (HashMapMetadata<K, V>) EMPTY;
        }

        try {
            Object[] table = FieldUtils.readField(map, "table", Object[].class);

            LinkedList<BucketMetadata<K, V>> collect = new LinkedList<>();
            for (int i = 0; i < table.length; i++) {
                BucketMetadata<K, V> bucket = analyseBucket(i, table[i], map);
                if (nonNull(bucket)) {
                    collect.add(bucket);
                }
            }

            return new HashMapMetadata<>(
                    table.length,
                    collect.size(),
                    collect
            );
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private BucketMetadata<K, V> analyseBucket(int bucketIndex, Object bucket, HashMap<K, V> map) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (isNull(bucket)) {
            return null;
        }

        NodeType bucketType = NodeType.find(bucket.getClass().getSimpleName());
        switch (bucketType) {
            case TREE_NODE:
                return analyseTreeNodeBucket(bucketIndex, bucket, map);
            case LINKED_LIST_NODE:
                return analyseListNodeBucket(bucketIndex, bucket, NodeType.LINKED_LIST_NODE);
            default:
                throw new IllegalArgumentException("Bucket type not supported in HashMap, bucketType = " + bucketType);
        }
    }

    private BucketMetadata<K, V> analyseTreeNodeBucket(int bucketIndex, Object bucket, HashMap<K, V> map) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Method untreeify = bucket.getClass().getDeclaredMethod("untreeify", HashMap.class);
        untreeify.setAccessible(true);
        Object nodeBucket = untreeify.invoke(bucket, map);

        return analyseListNodeBucket(bucketIndex, nodeBucket, NodeType.TREE_NODE);
    }

    private BucketMetadata<K, V> analyseListNodeBucket(int bucketIndex, Object bucket, NodeType nodeType) throws NoSuchFieldException, IllegalAccessException {
        if (isNull(bucket)) {
            return null;
        }

        LinkedList<NodeData<K, V>> collect = new LinkedList<>();
        NodeData<K, V> root = readNode(bucket);
        collect.add(root);

        Field next = FieldUtils.getField(bucket, "next");
        Object current = next.get(bucket);
        while (current != null) {
            NodeData<K, V> node = readNode(current);
            collect.add(node);
            current = next.get(current);
        }

        return new BucketMetadata<>(bucketIndex, nodeType, collect);
    }

    private NodeData<K, V> readNode(Object node) throws NoSuchFieldException, IllegalAccessException {
        K key = FieldUtils.readField(node, "key", keyClass);
        V value = FieldUtils.readField(node, "value", valueClass);
        int hashCode = FieldUtils.readField(node, "hash", Integer.class);

        return createNodeData(key, value, hashCode);
    }

    private NodeData<K, V> createNodeData(K key, V value, int hashCode) {
        return new NodeData<>(key, value, hashCode);
    }

}
