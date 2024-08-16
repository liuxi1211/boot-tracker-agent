package com.lcsc.agent.model;

import java.util.List;

public class TreeData {

    private String methodInfo;

    private List<TreeData> children;
    //占比
    private Double percent;

    //执行耗时
    private Double callTime;

    public String getMethodInfo() {
        return methodInfo;
    }

    public void setMethodInfo(String methodInfo) {
        this.methodInfo = methodInfo;
    }

    public List<TreeData> getChildren() {
        return children;
    }

    public void setChildren(List<TreeData> children) {
        this.children = children;
    }

    public Double getPercent() {
        return percent;
    }

    public void setPercent(Double percent) {
        this.percent = percent;
    }

    public Double getCallTime() {
        return callTime;
    }

    public void setCallTime(Double callTime) {
        this.callTime = callTime;
    }
}
