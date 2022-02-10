package com.wz.tinyweb.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class DispatcherServlet extends HttpServlet {

    public static class Api{
        public String path = null;
        public String classname = null;
        public String function = null;
    }

    private ArrayList<Api> apiList = new ArrayList<>();

    public ArrayList<Api> getApiList() {
        return apiList;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{
            String requestURI = request.getRequestURI();

            int count = apiList.size();
            for(int i=0;i<count;i++){
                Api api = apiList.get(i);
                if(requestURI.equals(api.path)){
                    /* get clazz by classname */
                    Class<?> clazz = Class.forName(api.classname);

                    /* get object from inject container */
                    String key = clazz.getSimpleName();
                    key = (new StringBuilder()).append(Character.toLowerCase(key.charAt(0))).append(key.substring(1)).toString();
                    Object object = InjectContainer.get(key);

                    /* get methods */
                    Method[] methods = clazz.getMethods();
                    for(Method method : methods){
                        /**
                         * find matched function to invoke
                         * comparition with:
                         *      1. function name
                         *      2. return type
                         *      3. parameter count and types
                         **/
                        if(method.getName().equals(api.function)){
                            if(method.getReturnType() == String.class){
                                Class<?>[] parameterTypes = method.getParameterTypes();
                                if (parameterTypes.length == 1){
                                    if(parameterTypes[0] == HttpServletRequest.class) {
                                        /* set method accessible & invoke */
                                        method.setAccessible(true);
                                        String responseContent = (String) method.invoke(object, request);

                                        response.addHeader("Access-Control-Allow-Origin","*");
                                        response.setCharacterEncoding("UTF-8");
                                        Writer writer = response.getWriter();
                                        writer.write(responseContent);
                                        writer.close();
                                        return;
                                    } else if (parameterTypes[0] == String.class){
                                        String requestContent = RequestUtil.getRequestContent(request);

                                        /* set method accessible & invoke */
                                        method.setAccessible(true);
                                        String responseContent = (String) method.invoke(object, requestContent);

                                        response.addHeader("Access-Control-Allow-Origin","*");
                                        response.setCharacterEncoding("UTF-8");
                                        Writer writer = response.getWriter();
                                        writer.write(responseContent);
                                        writer.close();
                                        return;
                                    } else if (parameterTypes[0] == JSONObject.class ||
                                            parameterTypes[0] == JSONArray.class){
                                        try{
                                            String requestContent = RequestUtil.getRequestContent(request);
                                            Object requestObject = JSON.parse(requestContent);

                                            /* set method accessible & invoke */
                                            method.setAccessible(true);
                                            String responseContent = (String) method.invoke(object, requestObject);

                                            response.addHeader("Access-Control-Allow-Origin","*");
                                            response.setCharacterEncoding("UTF-8");
                                            Writer writer = response.getWriter();
                                            writer.write(responseContent);
                                            writer.close();
                                            return;
                                        } catch (JSONException e){
                                            String exceptionDescript = "request content parse JSONObject failed with:\n\t";
                                            exceptionDescript += String.format("path:%s\n\t",api.path);
                                            exceptionDescript += String.format("class:%s\n\t",api.classname);
                                            exceptionDescript += String.format("function:%s",api.function);
                                            log(exceptionDescript,e);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            super.doGet(request,response);
        } catch (ClassNotFoundException |
                IllegalAccessException |
                InvocationTargetException |
                IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }

}
