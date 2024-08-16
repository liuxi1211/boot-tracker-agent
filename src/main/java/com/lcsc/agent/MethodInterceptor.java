package com.lcsc.agent;

import com.lcsc.agent.model.MethodCall;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MethodInterceptor {
    private static final ConcurrentLinkedQueue<MethodCall> callStacks = new ConcurrentLinkedQueue<>();

    private static final ThreadLocal<Integer> callDepth = ThreadLocal.withInitial(() -> 0);

    @RuntimeType
    public static Object intercept(@SuperCall Callable<?> zuper, @Origin Method method, @AllArguments Object[] args) throws Exception {

        Integer depth = callDepth.get();

        long startTime = System.currentTimeMillis();

        //调用深度+1
        callDepth.set(depth + 1);

        try {
            return zuper.call();
        } catch (NoSuchMethodError e) {
            System.out.println(e);
            throw e;
        } finally {

            long endTime = System.currentTimeMillis();

            if (endTime - startTime > AgentProperties.getIgnoretime()) {

                MethodCall call = new MethodCall(Thread.currentThread().getName(), method.getDeclaringClass().getName(), method.getName(), startTime, endTime, depth);

                if (args != null && args.length > 0) {
                    String[] argArr = new String[args.length];
                    for (int i = 0; i < args.length; i++) {
                        Object arg = args[i];
                        if (arg == null) {
                            argArr[i] = "null";
                        } else {
                            //基本类型
                            if (arg.getClass().isPrimitive()) {
                                argArr[i] = String.valueOf(arg);
                            } else if (arg.getClass().equals(String.class)) {
                                //是否为 String 类型
                                argArr[i] = (String) arg;
                            } else if (arg instanceof Class) {
                                argArr[i] = ((Class) arg).getName();
                            } else if (arg instanceof BigDecimal || arg instanceof Boolean || arg instanceof Long || arg instanceof Double || arg instanceof Integer || arg instanceof Float || arg instanceof Short || arg instanceof Byte) {
                                argArr[i] = arg.toString();
                            } else {
                                //引用类型
                                argArr[i] = arg.getClass().getName();
                            }
                        }
                    }
                    call.setArgs(argArr);
                }

                callStacks.add(call);

                call.setEndTime(endTime);

                //项目启动完成
                if (call.getCallDepth() == 0 && call.getThreadName().equals("main")) {
                    BootTrackerService.writeView();
                }
            }

            //调用深度-1
            callDepth.set(depth);
        }
    }

    public static ConcurrentLinkedQueue<MethodCall> getAllCallStack() {
        return callStacks;
    }
}

