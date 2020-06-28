package io.github.dejankos.hashmap.analyser.util;

import io.github.dejankos.hashmap.analyser.model.HashMapMetadata;

public class BucketSorter {

    public static <K, V> void sort(HashMapMetadata<K, V> hashMapMetadata) {
        hashMapMetadata.getBucketsMetadata().sort((o1, o2) -> {
            if (o1.getNodesData().size() == o2.getNodesData().size()) {
                return 0;
            }

            return o1.getNodesData().size() < o2.getNodesData().size() ? 1 : -1;
        });
    }
}
