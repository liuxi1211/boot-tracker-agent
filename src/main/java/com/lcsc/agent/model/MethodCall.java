package com.lcsc.agent.model;

public class MethodCall {
    private final String className;
    private final String methodName;
    private final long startTime;
    private long endTime;
    private int callDepth;
    private String threadName;
    private String[] args;
    public MethodCall(String threadName, String className, String methodName, long startTime, long endTime, int callDepth) {
        this.className = className;
        this.methodName = methodName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.callDepth = callDepth;
        this.threadName = threadName;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public long getDuration() {
        return endTime - startTime;
    }

    public int getCallDepth() {
        return callDepth;
    }

    public void setCallDepth(int callDepth) {
        this.callDepth = callDepth;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }
}