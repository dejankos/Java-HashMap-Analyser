package io.github.dejankos.hashmap.analyser;


import io.github.dejankos.hashmap.analyser.model.BucketMetadata;
import io.github.dejankos.hashmap.analyser.model.HashMapMetadata;
import io.github.dejankos.hashmap.analyser.model.NodeData;
import io.github.dejankos.hashmap.analyser.model.NodeType;
import io.github.dejankos.hashmap.analyser.util.FieldUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

public class HashMapAnalyser<K, V> {

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static final HashMapMetadata EMPTY = new HashMapMetadata(0, 0, emptyList());

    private final Class<K> keyClass;
    private final Class<V> valueClass;

    public HashMapAnalyser(Class<K> keyClass, Class<V> valueClass) {
        this.keyClass = keyClass;
        this.valueClass = valueClass;
    }

    public HashMapMetadata<K, V> analyse(HashMap<K, V> map) {
        if (map == null || map.isEmpty()) {
            //noinspection unchecked
            return (HashMapMetadata<K, V>) EMPTY;
        }

        try {
            Object[] table = FieldUtils.readField(map, "table", Object[].class);
            List<BucketMetadata<K, V>> bucketsMetadata = IntStream.range(0, table.length)
                    .mapToObj(i -> analyseBucket(i, table[i], map))
                    .filter(Objects::nonNull)
                    .collect(toList());

            return new HashMapMetadata<>(
                    table.length,
                    bucketsMetadata.size(),
                    bucketsMetadata
            );
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private BucketMetadata<K, V> analyseBucket(int bucketIndex, Object bucket, HashMap<K, V> map) {
        if (bucket == null) {
            return null;
        }

        try {
            NodeType bucketType = NodeType.find(bucket.getClass().getSimpleName());
            switch (bucketType) {
                case TREE_NODE:
                    return analyseTreeNodeBucket(bucketIndex, bucket, map);
                case LINKED_LIST_NODE:
                    return analyseListNodeBucket(bucketIndex, bucket, NodeType.LINKED_LIST_NODE);
                default:
                    throw new IllegalArgumentException("Bucket type not supported in HashMap, bucketType = " + bucketType);
            }
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e.getMessage(), e);
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

        List<NodeData<K, V>> collect = new ArrayList<>();
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
