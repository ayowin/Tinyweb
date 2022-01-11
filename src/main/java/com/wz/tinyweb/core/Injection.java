package com.wz.tinyweb.core;

import java.lang.reflect.Field;
import java.util.*;

public class Injection {

    private static class AutowiredNullException extends Throwable{
        public AutowiredNullException(String content){
            super(content);
        }
    }

    public static void scanAnnotation(String packageName){
        Set<Class<?>> classSet = ClassUtil.getClasses(packageName);
        if(classSet != null){
            parseAnnotation(classSet);
        }
    }

    private static void parseAnnotation(Set<Class<?>> classSet){
        Set<Class<?>> notPassClassSet = new HashSet<>();

        /* remove classes from classSet that do not meet the instantiation requirements */
        Iterator<Class<?>> classIterator = classSet.iterator();
        while (classIterator.hasNext()){
            Class<?> c = classIterator.next();
            Inject inject = c.getAnnotation(Inject.class);
            if(inject != null){
                Field[] fields = c.getDeclaredFields();
                if(fields != null){
                    for(Field field : fields){
                        Autowired autowired = field.getAnnotation(Autowired.class);
                        if(autowired != null){
                            String key = null;
                            if(autowired.value().trim().equals("")){
                                key = firstWordToLowerCase(field.getName());
                            } else {
                                key = autowired.value();
                            }
                            Object object = InjectContainer.get(key);
                            if(object == null){
                                notPassClassSet.add(c);
                                classIterator.remove();
                            }
                        }
                    }
                }
            } else {
                classIterator.remove();
            }
        }

        /* instantiate classes */
        if(classSet != null && classSet.size() > 0){
            for(Class<?> c : classSet){
                try {
                    Inject inject = c.getAnnotation(Inject.class);
                    Object object = c.newInstance();
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
                        key = inject.value();
                    }
                    Field[] fields = c.getDeclaredFields();
                    if(fields != null){
                        for(Field field : fields){
                            Autowired autowired = field.getAnnotation(Autowired.class);
                            if(autowired != null){
                                String fieldKey = null;
                                if(autowired.value().trim().equals("")){
                                    fieldKey = firstWordToLowerCase(field.getName());
                                } else {
                                    fieldKey = autowired.value();
                                }
                                Object fieldObject = InjectContainer.get(fieldKey);
                                field.setAccessible(true);
                                field.set(object,fieldObject);
                            }
                        }
                    }
                    InjectContainer.add(key,object);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                throw new AutowiredNullException(String.format("inject failed with classes %s",notPassClassSet.toString()));
            } catch (AutowiredNullException e) {
                e.printStackTrace();
                return;
            }
        }

        /* recursion for instantiate other classes */
        if(notPassClassSet != null && notPassClassSet.size() > 0){
            parseAnnotation(notPassClassSet);
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