package com.lcsc.agent.model;

public class ListData {

    //方法描述
    private String methodInfo;

    //执行时间
    private Double callTime;

    //本方法执行时间
    private Double selfCallTime;

    public String getMethodInfo() {
        return methodInfo;
    }

    public void setMethodInfo(String methodInfo) {
        this.methodInfo = methodInfo;
    }

    public Double getCallTime() {
        return callTime;
    }

    public void setCallTime(Double callTime) {
        this.callTime = callTime;
    }

    public Double getSelfCallTime() {
        return selfCallTime;
    }

    public void setSelfCallTime(Double selfCallTime) {
        this.selfCallTime = selfCallTime;
    }
}
