package com.wz.tinyweb.core;

import com.sun.org.apache.xerces.internal.xs.StringList;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class Injection {

    private static class AutowiredNullException extends Throwable{
        public AutowiredNullException(String content){
            super(content);
        }
    }

    public static void scanAnnotation(String packageName){
        parseAnnotation(ClassUtil.getClasses(packageName));
    }

    private static void parseAnnotation(Set<Class<?>> classSet){
        parseAnnotation(classSet,null);
    }

    private static void parseAnnotation(Set<Class<?>> classSet,List<String> nullObjectKeyList){
        if(classSet == null){
            return;
        }

        boolean recursion = false;

        ArrayList<String> keyList = new ArrayList<>();

        for(Class<?> c : classSet){
            try {
                /* parse class @Configuration */
                Configuration configuration = c.getAnnotation(Configuration.class);
                if(configuration != null) {
                    String key = firstWordToLowerCase(c.getSimpleName());
                    Object object = InjectContainer.get(key);
                    if (object == null) {
                        object = c.newInstance();
                        InjectContainer.add(key, object);
                    }
                    Field[] fields = c.getDeclaredFields();
                    if(fields != null){
                        for(Field field : fields) {
                            Autowired autowired = field.getAnnotation(Autowired.class);
                            if (autowired != null) {
                                String fieldKey = null;
                                if(autowired.value().trim().equals("")){
                                    fieldKey = firstWordToLowerCase(field.getName());
                                } else {
                                    fieldKey = autowired.value().trim();
                                }
                                Object fieldObject = InjectContainer.get(fieldKey);
                                if(fieldObject == null){
                                    recursion = true;
                                    if(!keyList.contains(fieldKey)){
                                        keyList.add(fieldKey);
                                    }
                                } else {
                                    field.setAccessible(true);
                                    field.set(object,fieldObject);
                                }
                            }
                        }
                    }
                    Method[] methods = c.getMethods();
                    if (methods != null) {
                        for(Method method : methods){
                            Inject inject = method.getAnnotation(Inject.class);
                            if(inject != null){
                                String methodObjectKey = firstWordToLowerCase(method.getName());
                                Object methodObject = InjectContainer.get(methodObjectKey);
                                if(methodObject == null){
                                    Parameter[] parameters = method.getParameters();
                                    if(parameters != null && parameters.length > 0){
                                        boolean allParametersInjected = true;
                                        Object[] parameterObjects = new Object[parameters.length];

                                        for(int i=0;i<parameters.length;i++){
                                            String parameterKey = firstWordToLowerCase(parameters[i].getName());
                                            parameterObjects[i] = InjectContainer.get(parameterKey);
                                            if(parameterObjects[i] == null){
                                                allParametersInjected = false;
                                                recursion = true;
                                                if(!keyList.contains(parameterKey)){
                                                    keyList.add(parameterKey);
                                                }
                                            }
                                        }

                                        if(allParametersInjected){
                                            methodObject = method.invoke(object,parameterObjects);
                                            if(methodObject != null){
                                                InjectContainer.add(methodObjectKey,methodObject);
                                            }
                                        }
                                    } else {
                                        methodObject = method.invoke(object,null);
                                        if(methodObject != null){
                                            InjectContainer.add(methodObjectKey,methodObject);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                /* parse class @Inject */
                Inject inject = c.getAnnotation(Inject.class);
                if(inject != null){
                    String key = null;
                    if(inject.value().trim().equals("")){
                        Class<?>[] interfaces = c.getInterfaces();
                        if(interfaces != null && interfaces.length == 1){
                            /* if implement only one interface , use interface name as key */
                            key = firstWordToLowerCase(interfaces[0].getSimpleName());
                        } else {
                            key = firstWordToLowerCase(c.getSimpleName());
                        }
                    } else {
                        key = inject.value().trim();
                    }
                    Object object = InjectContainer.get(key);
                    if(object == null){
                        object = c.newInstance();
                        InjectContainer.add(key,object);
                    }
                    Field[] fields = c.getDeclaredFields();
                    if(fields != null){
                        for(Field field : fields) {
                            Autowired autowired = field.getAnnotation(Autowired.class);
                            if (autowired != null) {
                                String fieldKey = null;
                                if(autowired.value().trim().equals("")){
                                    fieldKey = firstWordToLowerCase(field.getName());
                                } else {
                                    fieldKey = autowired.value().trim();
                                }
                                Object fieldObject = InjectContainer.get(fieldKey);
                                if(fieldObject == null){
                                    recursion = true;
                                    if(!keyList.contains(fieldKey)){
                                        keyList.add(fieldKey);
                                    }
                                } else {
                                    field.setAccessible(true);
                                    field.set(object,fieldObject);
                                }
                            }
                        }
                    }
                }
            } catch (IllegalAccessException |
                    InstantiationException |
                    InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        if(nullObjectKeyList != null){
            if(nullObjectKeyList.size() == keyList.size()){
                boolean allTheSame = true;
                for (int i=0;i<nullObjectKeyList.size();i++){
                    if (!nullObjectKeyList.get(i).equals(keyList.get(i))){
                        allTheSame = false;
                    }
                }
                try {
                    if (allTheSame) {
                        throw new AutowiredNullException(String.format("autowire failed with keys: ") + nullObjectKeyList.toString());
                    }
                } catch (AutowiredNullException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }

        if(recursion){
            parseAnnotation(classSet,keyList);
        }
    }

    private static String firstWordToLowerCase(String words){
        if(Character.isLowerCase(words.charAt(0))){
            return words;
        }
        else {
            return (new StringBuilder()).append(Character.toLowerCase(words.charAt(0))).append(words.substring(1)).toString();
        }
    }

}
