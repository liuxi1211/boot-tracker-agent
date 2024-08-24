package com.lcsc.agent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lcsc.agent.model.ListData;
import com.lcsc.agent.model.MethodCall;
import com.lcsc.agent.model.Node;
import com.lcsc.agent.model.TreeData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BootTrackerService {


    public static void writeView() {

        String filePath = AgentProperties.getPath() + File.separator + "BootTrackerView.html";
        System.out.println("启动耗时文件：" + filePath);

        try (InputStream inputStream = BootTrackerService.class.getClassLoader().getResourceAsStream("BootTrackerView.html");
             FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {

            //将文件流全部读取到 byte[]
            byte[] bytes = new byte[inputStream.available()];

            inputStream.read(bytes);

            String startView = new String(bytes, StandardCharsets.UTF_8);

            String treeData = getTreeData();
            String listData = getListData();
            String replace = startView.replace("'#treeData#'", treeData).replace("'#listData#'", listData);

            fileOutputStream.write(replace.getBytes(StandardCharsets.UTF_8));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getListData() {

        List<MethodCall> methodCalls = filterMainMethod();

        MethodCall root = methodCalls.get(0);

        List<ListData> listData = new ArrayList<>(methodCalls.size());
        for (MethodCall methodCall : methodCalls) {

            ListData item = new ListData();

            String methodInfo = getMethodInfo(methodCall);

            item.setMethodInfo(methodInfo);

            double callTime = (methodCall.getEndTime() - methodCall.getStartTime()) * 1d;

            //本方法执行时长
            item.setSelfCallTime(callTime / 1000);

            //方法截至执行时长
            item.setCallTime((methodCall.getEndTime() - root.getStartTime()) * 1d / 1000);

            listData.add(item);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        String listJson;
        try {
            listJson = objectMapper.writeValueAsString(listData);
        } catch (JsonProcessingException e) {
            System.out.println("序列化树状调用失败:" + e.getMessage());
            listJson = "[]";
        }
        return listJson;
    }

    /**
     * 过滤 main 线程的调用栈
     */
    private static List<MethodCall> filterMainMethod() {
        List<MethodCall> methodCalls = MethodInterceptor.getAllCallStack().stream().filter(item -> "main".equalsIgnoreCase(item.getThreadName()))
                .sorted((a, b) -> {
                    int i = (int) (a.getStartTime() - b.getStartTime());
                    if (i != 0) {
                        return i;
                    }
                    return a.getCallDepth() - b.getCallDepth();
                }).collect(Collectors.toList());
        return methodCalls;
    }

    private static String getTreeData() {

        Node root = getRootNode();

        TreeData tree = getTree(root, root.getValue().getEndTime() - root.getValue().getStartTime());

        //暂时只展示 main 方法
        ObjectMapper objectMapper = new ObjectMapper();
        String treeJson;
        try {
            treeJson = "[" + objectMapper.writeValueAsString(tree) + "]";
        } catch (JsonProcessingException e) {
            System.out.println("序列化方法列表失败:" + e.getMessage());
            treeJson = "[]";
        }
        return treeJson;
    }

    private static Node getRootNode() {

        List<MethodCall> methodCalls = filterMainMethod();

        //根节点
        Node root = new Node(methodCalls.get(0));

        //上一个节点
        Node preNode = root;
        for (int i = 1; i < methodCalls.size(); i++) {

            int preNodeDepth = preNode.getDepth();

            MethodCall methodCall = methodCalls.get(i);

            Node value;
            if (preNodeDepth == methodCall.getCallDepth()) {
                //和上个节点是平行节点
                List<Node> childs = preNode.getParent().getChilds();
                value = new Node(methodCall, preNode.getParent());
                childs.add(value);

            } else if (preNodeDepth < methodCall.getCallDepth()) {
                //上一个节点的子节点
                List<Node> childs = new ArrayList<>();
                value = new Node(methodCall, preNode);
                childs.add(value);
                preNode.setChilds(childs);

            } else {
                //向上递归
                while (preNode.getDepth() > methodCall.getCallDepth()) {
                    preNode = preNode.getParent();
                }
                value = new Node(methodCall, preNode.getParent());
                preNode.getParent().getChilds().add(value);
            }

            preNode = value;
        }

        return root;
    }

    private static TreeData getTree(Node node, double allTime) {

        MethodCall value = node.getValue();

        TreeData treeData = new TreeData();

        double callTime = (value.getEndTime() - value.getStartTime()) * 1d;
        //执行时间
        treeData.setCallTime(callTime / 1000);
        //比例
        treeData.setPercent(BigDecimal.valueOf(callTime).divide(BigDecimal.valueOf(allTime), 4, BigDecimal.ROUND_UP).multiply(BigDecimal.valueOf(100)).doubleValue());

        treeData.setMethodInfo(getMethodInfo(value));

        //获取子节点
        List<Node> childs = node.getChilds();

        if (childs != null && childs.size() > 0) {

            List<TreeData> childMethods = childs.stream().map(item -> getTree(item, allTime)).collect(Collectors.toList());
            treeData.setChildren(childMethods);
        }

        return treeData;
    }

    /**
     * 组装类、方法、参数
     *
     * @param methodCall
     * @return
     */
    private static String getMethodInfo(MethodCall methodCall) {

        //组装类、方法、参数
        String[] args = methodCall.getArgs();
        StringBuilder methodInfo = new StringBuilder(methodCall.getClassName());
        methodInfo.append(".");
        methodInfo.append(methodCall.getMethodName());
        methodInfo.append("(");
        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                methodInfo.append(arg);
                if (i != args.length - 1) {
                    methodInfo.append(",");
                }
            }
        }
        methodInfo.append(")");

        return methodInfo.toString();
    }
}