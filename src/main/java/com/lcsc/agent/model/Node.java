package com.lcsc.agent.model;

import java.util.List;

public class Node {

    private MethodCall value;

    private List<Node> childs;

    private Node parent;

    public Node(MethodCall value) {
        this.value = value;
    }

    public Node(MethodCall value, Node parent) {
        this.value = value;
        this.parent = parent;
    }

    public int getDepth() {

        return value.getCallDepth();
    }

    public MethodCall getValue() {
        return value;
    }

    public void setValue(MethodCall value) {
        this.value = value;
    }

    public List<Node> getChilds() {
        return childs;
    }

    public void setChilds(List<Node> childs) {
        this.childs = childs;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }
}
