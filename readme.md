[toc]



# Tinyweb

一个轻量级Java后端代码框架



## 1. 技术要点

* tomcat-embed
* 反射机制
* 注解



## 2. 搭建指南

* 纯maven工程，按maven搭建即可。



## 3. 使用文档
**示例：参考IndexController的实现结合，application.yml的配置**  
  
**IndexController.java如下：**
```java
package com.wz.tinyweb.controller;

import com.alibaba.fastjson.JSONObject;
import com.wz.tinyweb.core.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/index")
public class IndexController {

    public String index(HttpServletRequest request){
        return request.toString();
    }

    public String string(String request){
        return request;
    }

    public String json(JSONObject request){
        return request.toJSONString();
    }

    @RequestMapping("/annotation")
    public String annotation(HttpServletRequest request){
        return request.toString();
    }

}
```

### 3.1 配置方式
使用application.yml作为框架的配置文件，示例配置如下：
```yaml
tinyweb:
    app-base: webapps
    encode: UTF-8
    port: 8080
    context-path:
    api:
        - path: /index
          class: com.wz.tinyweb.controller.IndexController
          function: index
        - path: /string
          class: com.wz.tinyweb.controller.IndexController
          function: string
        - path: /json
          class: com.wz.tinyweb.controller.IndexController
          function: json
```
* tinyweb: 根节点
* app-base: 根目录名称，第一次运行服务会自动建立该目录
* encode: 字符集编码
* context-path: 上下文路径，默认值为ROOT，配置后，所有请求url需加上该前缀，如：context-path为/example时，所有的请求url为 http://localhost:8080/example/*
* api: 接口配置，类型为数组，每个元素有path、class、function属性，按需配置。
    * path: 请求url
    * class：实现该接口的全类名
    * function: 实现该接口的函数名

### 3.2 注解方式
接口除了在applicationi.yml中以配置的形式为其指定类和函数外，还支持以注解@RequestMapping的方式定义，取IndexConroller.java的代码如下：
```java
@RequestMapping("/index")
public class IndexController {
    @RequestMapping("/annotation")
    public String annotation(HttpServletRequest request){
        return request.toString();
    }
}
```
表示http://localhost/index/annotation请求由IndexController中的annotation函数处理。

### 3.3 接口函数要求
接口函数有3个要求，取IndexConroller.java的代码如下：
```java
public class IndexController {
    public String index(HttpServletRequest request){
        return request.toString();
    }

    public String string(String request){
        return request;
    }

    public String json(JSONObject request){
        return request.toJSONString();
    }
}
```
1. 返回值为String，请求参数为HttpServletRequest
2. 返回值为String，请求参数为String
3. 返回值为String，请求参数为JSONObject，其中JSONObject使用的是阿里fastjson库。