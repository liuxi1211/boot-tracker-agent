package com.lcsc.agent;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;

public class BootTrackerAgent {

    public static void premain(String agentArgs, Instrumentation inst) {

        //默认代理接口
        List<String> defaultInterfaceNames = getDefaultInterfaceNames();

        //默认代理名称
        List<String> defaultNames = getDefaultNames();

        //设置拓展参数
        setAgentArgs(agentArgs, defaultInterfaceNames, defaultNames);

        //注册拓展接口
        List<String> registryInterfaces = AgentProperties.getInterfaces();
        if (registryInterfaces.size() > 0) {
            registryInterfaces.stream().forEach(defaultInterfaceNames::add);
        }
        //注册拓展名称
        List<String> registryNames = AgentProperties.getNames();
        if (registryNames.size() > 0) {
            registryNames.stream().forEach(defaultNames::add);
        }

        //接口加载
        List<Class<?>> interfaces = new ArrayList<>();
        for (String interfaceName : defaultInterfaceNames) {
            try {
                // 动态获取接口类
                interfaces.add(Class.forName(interfaceName));
            } catch (ClassNotFoundException e) {
                System.out.println("Class not found: " + e.getMessage());
            }
        }

        new AgentBuilder.Default().type((target -> {

                    if (!interfaces.isEmpty()) {
                        for (Class<?> interfaceClass : interfaces) {
                            if (target.isAssignableTo(interfaceClass)) {
                                return true;
                            }
                        }
                    }

                    if (!defaultNames.isEmpty()) {
                        for (String className : defaultNames) {
                            if (target.getName().equals(className)) {
                                return true;
                            }
                        }
                    }
                    return false;
                })
        ).transform((builder, typeDescription, classLoader, module, protectionDomain) ->
                builder.method(ElementMatchers.not(ElementMatchers.named("equals"))
                                .and(ElementMatchers.not(ElementMatchers.named("hashCode")))
                                .and(ElementMatchers.not(ElementMatchers.named("toString")))
                                .and(ElementMatchers.isDeclaredBy(typeDescription)))
                        .intercept(MethodDelegation.to(MethodInterceptor.class))
        ).installOn(inst);
    }

    private static List<String> getDefaultNames() {
        List<String> defaultNames = new ArrayList<>();
        defaultNames.add("org.springframework.boot.SpringApplication");
        defaultNames.add("org.springframework.boot.BeanDefinitionLoader");
        defaultNames.add("org.springframework.context.expression.StandardBeanExpressionResolver");
        defaultNames.add("org.springframework.beans.support.ResourceEditorRegistrar");
        defaultNames.add("org.springframework.context.annotation.ClassPathBeanDefinitionScanner");
        defaultNames.add("org.springframework.context.annotation.AnnotatedBeanDefinitionReader");
        defaultNames.add("org.springframework.context.annotation.ConfigurationClassParser");
        defaultNames.add("org.springframework.context.annotation.ConfigurationClassBeanDefinitionReader");
        defaultNames.add("org.springframework.context.annotation.ComponentScanAnnotationParser");
        //版本兼容添加
        defaultNames.add("org.springframework.boot.SpringApplicationRunListeners");
        defaultNames.add("com.ctrip.framework.apollo.ConfigService");
        return defaultNames;
    }

    private static List<String> getDefaultInterfaceNames() {

        List<String> defaultInterfaceNames = new ArrayList<>();
        defaultInterfaceNames.add("org.springframework.boot.BootstrapRegistryInitializer");
        defaultInterfaceNames.add("org.springframework.boot.SpringApplicationRunListener");
        defaultInterfaceNames.add("org.springframework.core.env.ConfigurableEnvironment");
        defaultInterfaceNames.add("org.springframework.boot.ApplicationContextFactory");
        defaultInterfaceNames.add("org.springframework.beans.factory.BeanFactory");
        defaultInterfaceNames.add("org.springframework.context.ApplicationContextInitializer");
        defaultInterfaceNames.add("org.springframework.beans.factory.config.ConfigurableListableBeanFactory");
        defaultInterfaceNames.add("org.springframework.beans.factory.config.BeanFactoryPostProcessor");
        defaultInterfaceNames.add("org.springframework.beans.factory.config.BeanPostProcessor");
        defaultInterfaceNames.add("org.springframework.context.ApplicationListener");
        defaultInterfaceNames.add("org.springframework.beans.factory.Aware");
        defaultInterfaceNames.add("org.springframework.core.io.ResourceLoader");
        defaultInterfaceNames.add("org.springframework.context.ApplicationEventPublisher");
        defaultInterfaceNames.add("org.springframework.core.SmartClassLoader");
        defaultInterfaceNames.add("org.springframework.core.AliasRegistry");
        defaultInterfaceNames.add("org.springframework.core.metrics.ApplicationStartup");
        defaultInterfaceNames.add("org.springframework.context.MessageSource");
        defaultInterfaceNames.add("org.springframework.context.event.ApplicationEventMulticaster");
        defaultInterfaceNames.add("javax.servlet.ServletContext");
        defaultInterfaceNames.add("org.springframework.boot.web.server.WebServerFactory");
        defaultInterfaceNames.add("org.springframework.boot.web.servlet.ServletContextInitializer");
        defaultInterfaceNames.add("org.springframework.core.env.Environment");
        defaultInterfaceNames.add("org.springframework.context.ApplicationEvent");
        defaultInterfaceNames.add("org.springframework.context.Lifecycle");
        defaultInterfaceNames.add("org.springframework.context.ApplicationListener");
        defaultInterfaceNames.add("org.springframework.context.ApplicationContextInitializer");
        defaultInterfaceNames.add("org.springframework.context.annotation.ImportBeanDefinitionRegistrar");
        return defaultInterfaceNames;
    }

    private static void setAgentArgs(String agentArgs, List<String> defaultInterfaceNames, List<String> defaultNames) {

        if (agentArgs != null) {

            String[] args = agentArgs.split(",");
            for (String arg : args) {
                String[] keyValue = arg.split("=");
                if (keyValue.length != 2) {
                    break;
                }
                String key = keyValue[0];
                String value = keyValue[1];
                if (value == null || value.isEmpty()) {
                    break;
                }
                try {

                    switch (key) {
                        case "ignoretime":
                            int ignoreTime = Integer.parseInt(value);

                            if (ignoreTime < 0 || ignoreTime > 10000) {
                                System.out.println("ignoreTime 配置范围 0-10000（ms），默认 100（ms）");
                                break;
                            }

                            AgentProperties.setIgnoretime(ignoreTime);
                            break;
                        case "interfaces":
                            List<String> interfaces = AgentProperties.getInterfaces();
                            for (String interfaceName : value.split(";")) {
                                if (!defaultInterfaceNames.contains(interfaceName)) {
                                    interfaces.add(interfaceName);
                                }
                            }
                            break;
                        case "names":
                            List<String> names = AgentProperties.getNames();
                            for (String name : value.split(";")) {
                                if (!defaultNames.contains(name)) {
                                    names.add(name);
                                }
                            }
                            break;
                        case "path":
                            AgentProperties.setPath(value);
                            break;
                        default:
                            break;
                    }

                } catch (Exception e) {
                    System.out.println("参数配置有误，arg：" + arg);
                }
            }
        }
    }
}