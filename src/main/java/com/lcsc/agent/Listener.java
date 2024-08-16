package com.lcsc.agent;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.utility.JavaModule;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

public class Listener extends AgentBuilder.Listener.Adapter {

    @Override
    public void onDiscovery(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
    }

    @Override
    public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded, DynamicType dynamicType) {
        for (Map.Entry<TypeDescription, byte[]> entry : dynamicType.getAuxiliaryTypes().entrySet()) {
            saveClassFile(entry.getKey(), entry.getValue());
        }
        saveClassFile(typeDescription, dynamicType.getBytes());
    }

    private void saveClassFile(TypeDescription typeDescription, byte[] classBytes) {

        String fileName = "E:\\Download\\proxy3\\" + typeDescription.getName().substring(typeDescription.getName().lastIndexOf(".") + 1) + ".class";

        File file = new File(fileName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            fileOutputStream.write(classBytes);
            fileOutputStream.close();
        } catch (IOException e) {
            System.out.println("错误：" + typeDescription);
        }
    }

    @Override
    public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded) {
    }
}
