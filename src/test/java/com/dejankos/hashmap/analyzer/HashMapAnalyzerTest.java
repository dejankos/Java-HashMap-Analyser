package com.dejankos.hashmap.analyzer;

import com.dejankos.hashmap.analyzer.model.BucketMetadata;
import com.dejankos.hashmap.analyzer.model.HashMapMetadata;
import com.dejankos.hashmap.analyzer.model.NodeType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HashMapAnalyzerTest {

    @Test
    public void nullAndEmptyMap() {
        HashMapAnalyzer<Object, Object> analyzer = new HashMapAnalyzer<>(Object.class, Object.class);

        assertNull(analyzer.analyse(null));
        assertNotNull(analyzer.analyse((new HashMap<>())));
    }

    @DisplayName("should have optimal bucket distribution ")
    @Test
    public void listNodeBuckets() {
        HashMap<Integer, Integer> map = new HashMap<>(150);
        IntStream.range(0, 150)
                .boxed()
                .forEach(i -> map.put(i, i));

        HashMapAnalyzer<Integer, Integer> analyzer = new HashMapAnalyzer<>(Integer.class, Integer.class);
        HashMapMetadata<Integer, Integer> mapMetadata = analyzer.analyse(map);

        assertTrue(mapMetadata.getTotalBucketsCount() > 150);
        assertEquals(150, mapMetadata.getUsedBucketsCount());

        mapMetadata.getBucketsMetadata().forEach(bucketMetadata -> {
            assertEquals(NodeType.LINKED_LIST_NODE, bucketMetadata.getNodeType());
            assertEquals(1, bucketMetadata.getNodesData().size());
        });
    }

    @DisplayName("should have one balanced binary search tree as bucket")
    @Test
    public void treeNodeBuckets() {
        HashMap<BadHashCodeClass, Integer> map = new HashMap<>(150);
        IntStream.range(0, 150)
                .boxed()
                .forEach(i -> map.put(new BadHashCodeClass(i), i));

        HashMapAnalyzer<BadHashCodeClass, Integer> analyzer = new HashMapAnalyzer<>(BadHashCodeClass.class, Integer.class);
        HashMapMetadata<BadHashCodeClass, Integer> mapMetadata = analyzer.analyse(map);

        assertTrue(mapMetadata.getTotalBucketsCount() > 150);
        assertEquals(1, mapMetadata.getUsedBucketsCount());

        BucketMetadata<BadHashCodeClass, Integer> bucketMetadata = mapMetadata.getBucketsMetadata().get(0);

        assertEquals(NodeType.TREE_NODE, bucketMetadata.getNodeType());
        assertEquals(150, bucketMetadata.getNodesData().size());
    }

    public static class BadHashCodeClass {
        private final int value;

        public BadHashCodeClass(int value) {
            this.value = value;
        }

        @Override
        public int hashCode() {
            return 42;
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }
    }
}
