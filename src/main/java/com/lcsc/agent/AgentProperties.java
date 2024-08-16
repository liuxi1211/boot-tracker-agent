package com.lcsc.agent;

import java.util.ArrayList;
import java.util.List;

public class AgentProperties {

    //时间忽略阈值(ms)
    public static int ignoretime = 100;

    //拓展代理类、包
    public static List<String> names = new ArrayList<>();

    //拓展代理接口
    public static List<String> interfaces = new ArrayList<>();

    //跟踪分析文件路径
    public static String path = System.getProperty("java.io.tmpdir");

    public static int getIgnoretime() {
       return ignoretime;
    }

    public static void setIgnoretime(int ignoretime) {
       AgentProperties.ignoretime = ignoretime;
    }

    public static List<String> getNames() {
       return names;
    }

    public static void setNames(List<String> names) {
       AgentProperties.names = names;
    }

    public static List<String> getInterfaces() {
       return interfaces;
    }

    public static void setInterfaces(List<String> interfaces) {
       AgentProperties.interfaces = interfaces;
    }

    public static String getPath() {
       return path;
    }

    public static void setPath(String path) {
       AgentProperties.path = path;
    }
}