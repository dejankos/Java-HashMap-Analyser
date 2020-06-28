package io.github.dejankos.hashmap.analyser.model;

import java.util.List;

public class BucketMetadata<K, V> {
    private final int bucketIndex;
    private final NodeType nodeType;
    private final List<NodeData<K, V>> nodesData;

    public BucketMetadata(int bucketIndex, NodeType nodeType, List<NodeData<K, V>> nodesData) {
        this.bucketIndex = bucketIndex;
        this.nodeType = nodeType;
        this.nodesData = nodesData;
    }

    public int getBucketIndex() {
        return bucketIndex;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public List<NodeData<K, V>> getNodesData() {
        return nodesData;
    }

    @Override
    public String toString() {
        return "BucketMetadata{" +
                "bucketIndex=" + bucketIndex +
                ", nodeType=" + nodeType +
                ", nodesData=" + nodesData +
                '}';
    }
}
