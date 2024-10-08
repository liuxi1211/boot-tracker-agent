# boot-tracker-agent

**springboot 项目启动耗时分析工具**

### 1. 项目介绍

本项目用于分析 springboot 项目启动耗时分析, 采用 javaagent 技术, 通过代理 springboot 项目启动过程中关键类和接口, 收集方法耗时数据

**优点:**

1. 使用  [Byte Buddy](https://bytebuddy.net/#/tutorial) 做动态代理, 相比其他动态代理, 性能更优
2. 针对性代理, 针对关键类和接口做代理,避免全部代理, 性能损耗 10% 以内
3. 使用简便, 耗时分析数据不需要任何三方工具,可以直接通过浏览器查看

### 2. 版本选择

1. **版本选择, 根据 `SpringBoot` 版本区别** 

   如果项目本身没有依赖 `byte-buddy`则无需考虑, 直接使用 `boot-tracker-agent-dependencies.jar` 任何版本都可以

   如果项目本身依赖了 `byte-buddy`, 版本选择参考以下: 

   * SpringBoot 2.7.3 及其以上版本使用 boot-tracker-agent `2.7.3` 版本
   * SpringBoot 2.7.2 及其以下版本使用 boot-tracker-agent `2.7.2` 版本

   >注意:
   >
   >关于版本适配没有做过所有版本的测试, 上面给出的版本只是针对常用版本给出的结论
   >
   >如果使用过程中出现因 `byte-buddy`  版本导致的故障, 可以自行调整 `boot-tackker-agent` 项目依赖的 byte-buddy 版本, 调整成和项目依赖版本一致即可
   >
   >```java
   ><dependency>
   ><groupId>net.bytebuddy</groupId>
   ><artifactId>byte-buddy</artifactId>
   ><version>1.12.13</version>
   ></dependency>
   >```

2. **`boot-tracker-agent.jar` 和 `boot-tracker-agent-dependencies.jar` 选择**

   两个 jar 包区别就是 jar 包中是否包含依赖, 如果项目本身不包含 `byte-buddy` 依赖, 必须使用 `boot-tracker-agent-dependencies.jar` 


### 3. 使用方式

1. **项目启动参数指定 agent jar**

   ```properties
   -javaagent:C:\Users\windy\Desktop\origin-boot-tracker-agent.jar
   ```

2. **启动耗时文件获取**

   启动分析文件默认存放路径为系统临时目录

   `linux ` 系统中路径为: `/tmp/BootTrackerView.html`

   `windows ` 系统中路径为: `C:\Users\*\AppData\Local\Temp\\BootTrackerView.html`

   >本地调试, 控制台也会输出文件地址 启动耗时文件：`C:\Users\windy\AppData\Local\Temp\\BootTrackerView.html`

### 4. 启动耗时数据分析

启动耗时文件是一个 html 文件,使用浏览器直接打开, 主要有两个功能 

数据分析最好有一定源码基础，调用树本身可以当成一个火焰图看待，直接找到每个调用链路最深层耗时最多的方法，再通过方法名称和参数基本可以确定，如果最深层方法内部耗时仍然不明确，可以使用扩展参数 names 和 interfaces

1. **调用树展示**

   ![调用树](https://github.com/liuxi1211/boot-tracker-agent/blob/main/src/main/resources/images/tree.png)

2. **方法列表搜索**

   ![方法列表](https://github.com/liuxi1211/boot-tracker-agent/blob/main/src/main/resources/images/list.png)



### 5. 扩展参数

| 参数       | 描述                                                         | 案例                                                       |
| ---------- | ------------------------------------------------------------ | ---------------------------------------------------------- |
| ignoretime | 忽略耗时, 单位 ms, 默认值 100, 方法耗时低于指定值数据不采集,减小数据文件大小 | ignoretime=100                                             |
| names      | 扩展类, 当需要进一步分析某个类耗时,可以通过 names 配置, 多个用分号分割 | 例如：想进一步分析 apollo 耗时，可以添加 names=com.ctrip.framework.apollo.ConfigService             |
| interfaces | 扩展接口, 指定接口的所有实现类都将打印方法耗时,多个用分号分隔   | interfaces=org.springframework.context.ApplicationListener |
| path       | 自定义耗时数据文件路径, 默认系统临时目录                     | path=D:\JavaProject                                        |

综合配置案例：
```properties
-javaagent:C:\Users\windy\Desktop\boot-tracker-agent.jar=path=D:\JavaProject,names=com.ctrip.framework.apollo.ConfigService;com.xxx
```



