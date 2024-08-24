# boot-tracker-agent

**springboot 项目启动耗时分析**

### 1. 项目介绍

本项目用于分析 springboot 项目启动耗时, 主要采用 javaagent 技术, 代理 springboot 项目启动过程关键类和接口, 收集方法耗时并通过调用树展示

### 2. 使用方式

1. 调整 `byte-buddy` 依赖版本

   本项目依赖 `byte-buddy` 做动态代理 ,如果您的 springboot 项目本身依赖了 `byte-buddy` ,需要调整对应依赖版本和项目版本一致

   一种简单用法, 如果 springboot 版本高于 `2.7.3` 使用 `2.7.3` 版本, 否则使用 `2.7.2` 版本

2. 项目启动参数添加指定 agent jar

   ```properties
   -Xmx4g
   -Xms4g
   -javaagent:C:\Users\windy\Desktop\origin-boot-tracker-agent.jar
   ```

3. s 

