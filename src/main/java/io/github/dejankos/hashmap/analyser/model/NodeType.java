package io.github.dejankos.hashmap.analyser.model;

import java.util.HashMap;
import java.util.Map;

import static java.util.Optional.ofNullable;

public enum NodeType {
    LINKED_LIST_NODE("Node"),
    TREE_NODE("TreeNode");

    private static final Map<String, NodeType> lookup = new HashMap<>();

    static {
        for (NodeType nt : NodeType.values()) {
            lookup.put(nt.mapNodeName, nt);
        }
    }

    private final String mapNodeName;

    NodeType(String mapNodeName) {
        this.mapNodeName = mapNodeName;
    }

    public static NodeType find(String mapNodeName) {
        return ofNullable(lookup.get(mapNodeName))
                .orElseThrow(() -> new IllegalArgumentException("Node type not found for mapNodeName = " + mapNodeName));
    }
}
