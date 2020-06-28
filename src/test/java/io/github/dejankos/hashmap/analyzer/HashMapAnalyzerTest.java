package io.github.dejankos.hashmap.analyzer;

import io.github.dejankos.hashmap.analyzer.model.BucketMetadata;
import io.github.dejankos.hashmap.analyzer.model.HashMapMetadata;
import io.github.dejankos.hashmap.analyzer.model.NodeType;
import io.github.dejankos.hashmap.analyzer.util.BucketSorter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HashMapAnalyzerTest {

    private static final String ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @Test
    public void nullAndEmptyMap() {
        HashMapAnalyzer<Object, Object> analyzer = new HashMapAnalyzer<>(Object.class, Object.class);

        assertNotNull(analyzer.analyse(null));
        assertNotNull(analyzer.analyse((new HashMap<>())));
    }

    @DisplayName("should have optimal bucket distribution ")
    @Test
    public void listNodeBuckets() {
        HashMap<Integer, Integer> map = new HashMap<>(256);
        IntStream.range(0, 150)
                .boxed()
                .forEach(i -> map.put(i, i));

        HashMapAnalyzer<Integer, Integer> analyzer = new HashMapAnalyzer<>(Integer.class, Integer.class);
        HashMapMetadata<Integer, Integer> mapMetadata = analyzer.analyse(map);

        assertEquals(256, mapMetadata.getTotalBucketsCount());
        assertEquals(150, mapMetadata.getUsedBucketsCount());

        mapMetadata.getBucketsMetadata().forEach(bucketMetadata -> {
            assertEquals(NodeType.LINKED_LIST_NODE, bucketMetadata.getNodeType());
            assertEquals(1, bucketMetadata.getNodesData().size());
        });
    }

    @DisplayName("should have one balanced binary search tree as bucket")
    @Test
    public void treeNodeBuckets() {
        HashMap<BadHashCodeClass, Integer> map = new HashMap<>(256);
        IntStream.range(0, 150)
                .boxed()
                .forEach(i -> map.put(new BadHashCodeClass(i), i));

        HashMapAnalyzer<BadHashCodeClass, Integer> analyzer = new HashMapAnalyzer<>(BadHashCodeClass.class, Integer.class);
        HashMapMetadata<BadHashCodeClass, Integer> mapMetadata = analyzer.analyse(map);

        assertEquals(256, mapMetadata.getTotalBucketsCount());
        assertEquals(1, mapMetadata.getUsedBucketsCount());

        BucketMetadata<BadHashCodeClass, Integer> bucketMetadata = mapMetadata.getBucketsMetadata().get(0);

        assertEquals(NodeType.TREE_NODE, bucketMetadata.getNodeType());
        assertEquals(150, bucketMetadata.getNodesData().size());
    }

    @DisplayName("should sort buckets")
    @Test
    public void sortBuckets() {
        HashMap<String, Integer> map = new HashMap<>(150);
        IntStream.range(0, 150)
                .boxed()
                .forEach(i -> map.put(randomize(ALPHA), i));

        HashMapAnalyzer<String, Integer> analyzer = new HashMapAnalyzer<>(String.class, Integer.class);
        HashMapMetadata<String, Integer> mapMetadata = analyzer.analyse(map);

        BucketSorter.sort(mapMetadata);

        assertTrue(mapMetadata.getTotalBucketsCount() > 150);
        assertTrue(mapMetadata.getUsedBucketsCount() < 150);
        assertTrue(mapMetadata.getBucketsMetadata().get(0).getNodesData().size() > 1);
        assertEquals(1, mapMetadata.getBucketsMetadata().get(mapMetadata.getUsedBucketsCount() - 1).getNodesData().size());
    }

    private String randomize(String s) {
        StringBuilder sb = new StringBuilder();
        Random rnd = new Random();
        while (sb.length() < s.length()) {
            int index = (int) (rnd.nextFloat() * s.length());
            sb.append(s.charAt(index));
        }
        return sb.toString();
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
