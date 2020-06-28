package io.github.dejankos.hashmap.analyser.model;

import java.util.List;

public class HashMapMetadata<K, V> {
    private final int totalBucketsCount;
    private final int usedBucketsCount;
    private final List<BucketMetadata<K, V>> bucketsMetadata;

    public HashMapMetadata(int totalBucketsCount, int usedBucketsCount, List<BucketMetadata<K, V>> bucketsMetadata) {
        this.totalBucketsCount = totalBucketsCount;
        this.usedBucketsCount = usedBucketsCount;
        this.bucketsMetadata = bucketsMetadata;
    }

    public int getTotalBucketsCount() {
        return totalBucketsCount;
    }

    public int getUsedBucketsCount() {
        return usedBucketsCount;
    }

    public List<BucketMetadata<K, V>> getBucketsMetadata() {
        return bucketsMetadata;
    }

    @Override
    public String toString() {
        return "HashMapMetadata{" +
                "totalBucketsCount=" + totalBucketsCount +
                ", usedBucketsCount=" + usedBucketsCount +
                ", bucketsMetadata=" + bucketsMetadata +
                '}';
    }
}
