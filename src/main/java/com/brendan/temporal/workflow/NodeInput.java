package com.brendan.temporal.workflow;

public class NodeInput {
    private int 
        offset,
        length,
        depth,
        maxItemsPerLeaf,
        maxChildren,
        maxDepth;

    public NodeInput() {}

    public NodeInput(int offset, int length, int depth, int maxItemsPerLeaf, int maxChildren, int maxDepth) {
        this.offset = offset;
        this.length = length;
        this.depth = depth;
        this.maxItemsPerLeaf = maxItemsPerLeaf;
        this.maxChildren = maxChildren;
        this.maxDepth = maxDepth;
    }

    public int getOffset() {
        return offset;
    }

    public int getLength() {
        return length;
    }

    public int getDepth() {
        return depth;
    }

    public int getMaxItemsPerLeaf() {
        return maxItemsPerLeaf;
    }

    public int getMaxChildren() {
        return maxChildren;
    }

    public int getMaxDepth() {
        return maxDepth;
    }
        
    @Override
    public String toString() {
        return super.toString() + " | offset:" + offset + ", length:" + length + 
            ", depth:" + depth + ", maxItemsPerLeaf:" + maxItemsPerLeaf + 
            ", maxChildren:" + maxChildren + ", maxDepth:" + maxDepth;
    }
}
