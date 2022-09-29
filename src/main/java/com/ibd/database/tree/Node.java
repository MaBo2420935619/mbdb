package com.ibd.database.tree;

import java.util.List;

/*节点类*/
public class Node {

    //节点的子节点
    private List<Node> nodes;
    //节点的键值对
    private List<KeyAndValue> keyAndValue;
    //节点的后节点
    private Node nextNode;
    //节点的前节点
    private Node previousNode;
    //节点的父节点
    private Node parantNode;

    public Node( List<Node> nodes, List<KeyAndValue> keyAndValue, Node nextNode,Node previousNode, Node parantNode) {
        this.nodes = nodes;
        this.keyAndValue = keyAndValue;
        this.nextNode = nextNode;
        this.parantNode = parantNode;
        this.previousNode = previousNode;
    }

    boolean isLeaf() {
        return nodes==null;
    }

    boolean isHead() {
        return previousNode == null;
    }

    boolean isTail() {
        return nextNode == null;
    }

    boolean isRoot() {
        return parantNode == null;
    }


    List<Node> getNodes() {
        return nodes;
    }

    void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }


    List<KeyAndValue> getKeyAndValue() {
        return keyAndValue;
    }

//    public void setKeyAndValue(List<KeyAndValue> KeyAndValue) {
//        this.keyAndValue = KeyAndValue;
//    }

    Node getNextNode() {
        return nextNode;
    }

    void setNextNode(Node nextNode) {
        this.nextNode = nextNode;
    }

    Node getParantNode() {
        return parantNode;
    }

    void setParantNode(Node parantNode) {
        this.parantNode = parantNode;
    }

    Node getPreviousNode() {
        return previousNode;
    }

    void setPreviousNode(Node previousNode) {
        this.previousNode = previousNode;
    }
}
