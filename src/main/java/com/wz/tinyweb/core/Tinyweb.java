package com.wz.tinyweb.core;

import com.wz.tinyweb.Application;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Tinyweb {

    private Tomcat tomcat = new Tomcat();

    private String appBase = "webapps";
    private String encode = "UTF-8";
    private int port = 8080;
    private String contextPath = "";
    private DispatcherServlet dispatcherServlet = new DispatcherServlet();

    private static Tinyweb tinyweb = null;

    private Tinyweb() {
        loadConfig();
    }

    public static Tinyweb singleton(){
        if(tinyweb == null){
            tinyweb = new Tinyweb();
        }
        return tinyweb;
    }

    public void execute(){
        try {
            /* set app base dir: attention that this function must call first time */
            tomcat.setBaseDir(appBase);

            /* set port and uri encoding */
            Connector connector = tomcat.getConnector();
            connector.setPort(port);
            connector.setURIEncoding(encode);

            /* set app context */
            Context context = tomcat.addContext(contextPath,null);

            /* set dispatcher servlet */
            Wrapper wrapper = tomcat.addServlet(contextPath,"dispatcherServlet",dispatcherServlet);
            wrapper.addMapping("/*");
            wrapper.setLoadOnStartup(1);

            tomcat.start();
            tomcat.getServer().await();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
    }

    private void loadConfig(){
        try{
            /* read config file */
            InputStream inputStream = this.getClass().getResourceAsStream("/application.yml");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer content = new StringBuffer();
            String line = "";
            line = bufferedReader.readLine();
            while (line != null){
                content.append(line);
                content.append('\n');
                line = "";
                line = bufferedReader.readLine();
            }

            /* parse config */
            Yaml yaml = new Yaml();
            HashMap<String,Object> configMap = yaml.load(content.toString());
            HashMap<String,Object> tinywebMap = (HashMap<String,Object>)configMap.get("tinyweb");
            String appBaseValue = (String)tinywebMap.get("app-base");
            if(appBaseValue != null){
                appBase = appBaseValue;
            }
            String encodeValue = (String)tinywebMap.get("encode");
            if(encodeValue != null){
                encode = encodeValue;
            }
            Integer portValue = (Integer)tinywebMap.get("port");
            if(portValue != null){
                port = portValue;
            }
            String contextPathValue = (String)tinywebMap.get("context-path");
            if(contextPathValue != null){
                contextPath = contextPathValue;
            }
            List<Object> apiList = (List<Object>)tinywebMap.get("api");
            for (Object object : apiList){
                HashMap<String,Object> apiMap = (HashMap<String,Object>)object;
                String path = (String)apiMap.get("path");
                String classname = (String)apiMap.get("class");
                String function = (String)apiMap.get("function");

                DispatcherServlet.Api api = new DispatcherServlet.Api();
                api.path = path;
                api.classname = classname;
                api.function = function;
                dispatcherServlet.getApiList().add(api);
            }

            /* scan annotation: RequestMapping */
            Set<Class<?>> classSet = ClassUtil.getClasses(Application.class.getPackage().getName());
            for(Class c : classSet){
                String prefix = "";
                Annotation annotation = c.getAnnotation(RequestMapping.class);
                RequestMapping requestMapping = (RequestMapping) annotation;
                if(annotation != null){
                    prefix = requestMapping.value();
                }
                Method[] methods = c.getMethods();
                for(Method method : methods){
                    annotation = method.getAnnotation(RequestMapping.class);
                    if(annotation != null){
                        requestMapping = (RequestMapping) annotation;
                        String path = requestMapping.value();
                        String classname = c.getName();
                        String function = method.getName();

                        DispatcherServlet.Api api = new DispatcherServlet.Api();
                        api.path = prefix + path;
                        api.classname = classname;
                        api.function = function;
                        dispatcherServlet.getApiList().add(api);
                    }
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
