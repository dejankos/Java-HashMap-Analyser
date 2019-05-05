package com.dejankos.hashmap.analyzer.model;

public enum NodeType {
    LINKED_LIST_NODE("Node"),
    TREE_NODE("TreeNode");

    private final String mapNodeName;

    NodeType(String mapNodeName) {
        this.mapNodeName = mapNodeName;
    }

    public static NodeType find(String mapNodeName) {
        for (NodeType value : NodeType.values()) {
            if (mapNodeName.equals(value.mapNodeName)) {
                return value;
            }
        }

        throw new IllegalArgumentException("Node type not found for mapNodeName = " + mapNodeName);
    }

}
